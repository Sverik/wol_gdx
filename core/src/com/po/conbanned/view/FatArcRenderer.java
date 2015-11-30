package com.po.conbanned.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.po.conbanned.model.World;
import com.sun.media.jfxmediaimpl.MediaDisposer;

public class FatArcRenderer implements MediaDisposer.Disposable {

	public enum ShapeType {
		Point(GL20.GL_POINTS), Line(GL20.GL_LINE_STRIP), Filled(GL20.GL_TRIANGLE_STRIP);

		private final int glType;

		ShapeType (int glType) {
			this.glType = glType;
		}

		public int getGlType () {
			return glType;
		}
	}

	protected static final int X = 0;
	protected static final int Y = 1;

	private final ImmediateModeRenderer renderer;
	private boolean matrixDirty = false;
	private final Matrix4 projectionMatrix = new Matrix4();
	private final Matrix4 transformMatrix = new Matrix4();
	private final Matrix4 combinedMatrix = new Matrix4();
	private final Color color = new Color(1, 1, 1, 1);
	private ShapeType shapeType;

	private World world;

	public FatArcRenderer(World world) {
		this(5000);
		this.world = world;
	}

	public FatArcRenderer(int maxVertices) {
		renderer = new ImmediateModeRenderer20(maxVertices, false, true, 0);
		projectionMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}


	/** Starts a new batch of shapes. Shapes drawn within the batch will attempt to use the type specified. The call to this method
	 * must be paired with a call to {@link #end()}. */
	public void begin (ShapeType type) {
		if (shapeType != null) throw new IllegalStateException("Call end() before beginning a new shape batch.");
		shapeType = type;
		if (matrixDirty) {
			combinedMatrix.set(projectionMatrix);
			Matrix4.mul(combinedMatrix.val, transformMatrix.val);
			matrixDirty = false;
		}
		renderer.begin(combinedMatrix, shapeType.getGlType());
	}

	/** Finishes the batch of shapes and ensures they get rendered. */
	public void end () {
		renderer.end();
		shapeType = null;
	}

	/** @param other May be null. */
	private void check (ShapeType preferred, ShapeType other, int newVertices) {
		if (shapeType == null) throw new IllegalStateException("begin must be called first.");

		if (shapeType != preferred && shapeType != other) {
			// Shape type is not valid.
			if (other == null)
				throw new IllegalStateException("Must call begin(ShapeType." + preferred + ").");
			else
				throw new IllegalStateException("Must call begin(ShapeType." + preferred + ") or begin(ShapeType." + other + ").");
		} else if (matrixDirty) {
			// Matrix has been changed.
			ShapeType type = shapeType;
			end();
			begin(type);
		} else if (renderer.getMaxVertices() - renderer.getNumVertices() < newVertices) {
			// Not enough space.
			ShapeType type = shapeType;
			end();
			begin(type);
		}
	}

	/** Sets the projection matrix to be used for rendering. Usually this will be set to {@link Camera#combined}.
	 * @param matrix */
	public void setProjectionMatrix (Matrix4 matrix) {
		projectionMatrix.set(matrix);
		matrixDirty = true;
	}

	/** Sets the color to be used by the next shapes drawn. */
	public void setColor (float r, float g, float b, float a) {
		this.color.set(r, g, b, a);
	}

	/** Draws an arc using {@link ShapeType#Line} or {@link ShapeType#Filled}. */
	public void arc (float x, float y, float innerRadius, float outerRadius, float start, float degrees, int segments) {
		if (segments <= 0) throw new IllegalArgumentException("segments must be > 0.");

		float theta = (2 * MathUtils.PI * (degrees / 360.0f)) / segments;
		float cos = MathUtils.cos(theta);
		float sin = MathUtils.sin(theta);
		float[][] ci = {
				{innerRadius * MathUtils.cos(start * MathUtils.degreesToRadians), innerRadius * MathUtils.sin(start * MathUtils.degreesToRadians)},
				{0f, 0f},
				{0f, 0f},
		};
		float[][] co = {
				{outerRadius * MathUtils.cos(start * MathUtils.degreesToRadians), outerRadius * MathUtils.sin(start * MathUtils.degreesToRadians)},
				{0f, 0f},
				{0f, 0f},
		};

		if (shapeType == ShapeType.Line) {
			check(ShapeType.Line, ShapeType.Filled, segments * 4 + 2);

			// first wall
			renderer.color(color);
			renderer.vertex(x + co[0][X], y + co[0][Y], 0);
			renderer.color(color);
			renderer.vertex(x + ci[0][X], y + ci[0][Y], 0);
			for (int i = 0; i < segments; i++) {
				nextInArc(co[0], co[1], sin, cos);
				nextInArc(ci[0], ci[1], sin, cos);
				// inner arc
				renderer.color(color);
				renderer.vertex(x + ci[1][X], y + ci[1][Y], 0);
				// diagonal
				renderer.color(color);
				renderer.vertex(x + co[0][X], y + co[0][Y], 0);
				// outer arc
				renderer.color(color);
				renderer.vertex(x + co[1][X], y + co[1][Y], 0);
				// wall
				renderer.color(color);
				renderer.vertex(x + ci[1][X], y + ci[1][Y], 0);

				ci[0][X] = ci[1][X];
				ci[0][Y] = ci[1][Y];
				co[0][X] = co[1][X];
				co[0][Y] = co[1][Y];
			}
//			renderer.color(color);
//			renderer.vertex(x + ci[0][X], y + ci[0][Y], 0);
		} else {
			check(ShapeType.Line, ShapeType.Filled, segments * 2 + 2);

			// first wall
			renderer.color(color);
			renderer.vertex(x + ci[0][X], y + ci[0][Y], 0);
			renderer.color(color);
			renderer.vertex(x + co[0][X], y + co[0][Y], 0);
			for (int i = 0; i < segments; i++) {
				nextInArc(co[0], co[1], sin, cos);
				nextInArc(ci[0], ci[1], sin, cos);
				// diagonal
				renderer.color(color);
				renderer.vertex(x + ci[1][X], y + ci[1][Y], 0);
				// wall
				renderer.color(color);
				renderer.vertex(x + co[1][X], y + co[1][Y], 0);

				ci[0][X] = ci[1][X];
				ci[0][Y] = ci[1][Y];
				co[0][X] = co[1][X];
				co[0][Y] = co[1][Y];
			}
			renderer.color(color);
			renderer.vertex(x, y, 0);
			renderer.color(color);
//			renderer.vertex(x + cx, y + cy, 0);
		}

//		float temp = cx;
//		cx = 0;
//		cy = 0;
//		renderer.color(color);
//		renderer.vertex(x, y, 0);
	}

	protected static void nextInArc(float[] current, float[] next, float sin, float cos) {
		next[X] = cos * current[X] - sin * current[Y];
		next[Y] = sin * current[X] + cos * current[Y];
	}

	/** Draws an arc using {@link ShapeType#Line} or {@link ShapeType#Filled}. */
	public void arc (float x, float y, float radius, float start, float degrees, int segments) {
		if (segments <= 0) throw new IllegalArgumentException("segments must be > 0.");

		float theta = (2 * MathUtils.PI * (degrees / 360.0f)) / segments;
		float cos = MathUtils.cos(theta);
		float sin = MathUtils.sin(theta);
		float cx = radius * MathUtils.cos(start * MathUtils.degreesToRadians);
		float cy = radius * MathUtils.sin(start * MathUtils.degreesToRadians);

		if (shapeType == ShapeType.Line) {
			check(ShapeType.Line, ShapeType.Filled, segments * 2 + 2);

			renderer.color(color);
			renderer.vertex(x, y, 0);
			renderer.color(color);
			renderer.vertex(x + cx, y + cy, 0);
			world.debug("c0 x=" + cx + ", y=" + cy);
			for (int i = 0; i < segments; i++) {
				renderer.color(color);
				renderer.vertex(x + cx, y + cy, 0);
				float temp = cx;
				cx = cos * cx - sin * cy;
				cy = sin * temp + cos * cy;
				world.debug("c" + (i + 1) + " x=" + cx + ", y=" + cy);
				renderer.color(color);
				renderer.vertex(x + cx, y + cy, 0);
			}
			renderer.color(color);
			renderer.vertex(x + cx, y + cy, 0);
		} else {
			check(ShapeType.Line, ShapeType.Filled, segments * 3 + 3);

			for (int i = 0; i < segments; i++) {
				renderer.color(color);
				renderer.vertex(x, y, 0);
				renderer.color(color);
				renderer.vertex(x + cx, y + cy, 0);
				float temp = cx;
				cx = cos * cx - sin * cy;
				cy = sin * temp + cos * cy;
				renderer.color(color);
				renderer.vertex(x + cx, y + cy, 0);
			}
			renderer.color(color);
			renderer.vertex(x, y, 0);
			renderer.color(color);
			renderer.vertex(x + cx, y + cy, 0);
		}

		float temp = cx;
		cx = 0;
		cy = 0;
		renderer.color(color);
		renderer.vertex(x + cx, y + cy, 0);
	}

	@Override
	public void dispose() {
		renderer.dispose();
	}
}
