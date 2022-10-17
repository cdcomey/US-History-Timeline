
import Input from './js/input/input.js'
import { hex2rgb, deg2rad, normalize, dot, cross } from './js/utils/utils.js'
import { Box } from './js/app/object3d.js'

class Mat4 {

    /**
     * Creates an identity matrix
     * 
     * @returns {Mat4} An identity matrix
     */
    static identity() {
        return new Mat4(
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
        );
    }

    /**
     * Creates a translation matrix
     * 
     * @param {Number} x Translation in X direction
     * @param {Number} y Translation in Y direction
     * @param {Number} z Translation in Z direction
     * @returns {Mat4} A translation matrix
     */
    static translation( x, y, z ) {
        return new Mat4(
			1, 0, 0, x,
			0, 1, 0, y,
			0, 0, 1, z,
			0, 0, 0, 1);
    }

    /**
     * Creates a rotation matrix for X rotations
     * 
     * @param {Number} deg Angle in degrees
     * @returns {Mat4} A X rotation matrix
     */
    static rotationx( deg ) {
		var rad = deg2rad(deg);
		var c = Math.cos(rad);
		var s = Math.sin(rad);
        return new Mat4(
			1, 0, 0, 0,
			0, c, -s, 0,
			0, s, c, 0,
			0, 0, 0, 1
		);
    }

    /**
     * Creates a rotation matrix for Y rotations
     * 
     * @param {Number} deg Angle in degrees
     * @returns {Mat4} A Y rotation matrix
     */
    static rotationy( deg ) {
       var rad = deg2rad(deg);
		var c = Math.cos(rad);
		var s = Math.sin(rad);
        return new Mat4(
			c, 0, s, 0,
			0, 1, 0, 0,
			-s, 0, c, 0,
			0, 0, 0, 1
		);
    }

    /**
     * Creates a rotation matrix for Z rotations
     * 
     * @param {Number} deg Angle in degrees
     * @returns {Mat4} A Z rotation matrix
     */
    static rotationz( deg ) {
        var rad = deg2rad(deg);
		var c = Math.cos(rad);
		var s = Math.sin(rad);
        return new Mat4(
			c, -s, 0, 0,
			s, c, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1
		);
    }

    /**
     * Creates a scaling matrix
     * 
     * @param {Number} s The factor to scale by
     * @returns {Mat4} A scaling matrix
     */
    static scale( s ) {
        return new Mat4(s, 0, 0, 0,
									0, s, 0, 0,
									0, 0, s, 0,
									0, 0, 0, 1);
    }

    /**
     * Creates a view matrix using eye position and viewing target position
     * 
     * @param {Array<Number>} eye Position of the camera as list of [x,y,z]
     * @param {Array<Number>} center Position of the viewing target point as list of [x,y,z]
     * @param {Array<Number>} up Direction of the up vector - this is usually the Y axis
     * @returns {Mat4} A view matrix
     */
    static lookat( eye, center, up ) {
		// console.log('eye ' + eye[0] + ' ' + eye[1] + ' ' + eye[2]);
		// console.log('center ' + center[0] + ' ' + center[1] + ' ' + center[2]);
		var N = [eye[0] - center[0], eye[1] - center[1], eye[2] - center[2]];
		// console.log('N ' + N[0] + ' ' + N[1] + ' ' + N[2]);
		// console.log('normalize ' + normalize(N));
		var n = normalize(N);
		// console.log('n ' + n[0] + ' ' + n[1] + ' ' + n[2]);
		// console.log('up ' + up[0] + ' ' + up[1] + ' ' + up[2]);
		var U = cross(up, n);
		// console.log('U ' + U[0] + ' ' + U[1] + ' ' + U[2]);
		var u = normalize(U);
		// console.log('u ' + u[0] + ' ' + u[1] + ' ' + u[2]);
		var v = cross(n, u);
		// console.log('v ' + v[0] + ' ' + v[1] + ' ' + v[2]);
		
        return new Mat4(
			u[0], u[1], u[2], 0,
			v[0], v[1], v[2], 0,
			n[0], n[1], n[2], 0,
			0, 0, 0, 1
		).multiply(new Mat4(
			1, 0, 0, -1*eye[0],
			0, 1, 0, -1*eye[1],
			0, 0, 1, -1*eye[2],
			0, 0, 0, 1
		));
		
		/*
		return new Mat4(
			u[0], v[0], n[0], 0,
			u[1], v[1], n[1], 0,
			u[2], v[2], n[2], 0,
			0, 0, 0, 1
		)
		*/
    }

    /**
     * Creates a projection matrix using perspective projection
     * 
     * @param {Number} fovy Vertical field of view in degrees
     * @param {Number} aspect Aspect ratio of the canvas (width / height)
     * @param {Number} near Near plane distance
     * @param {Number} far Far plane distance
     * @returns {Mat4} A perspective projection matrix
     */
    static projectionPerspective( fovy, aspect, near, far ) {
		var xmin = -near * Math.tan(deg2rad(fovy/2));
		// console.log('xmin = ' + xmin);
		var xmax = near * Math.tan(deg2rad(fovy/2));
		// console.log('xmax = ' + xmax);
		var angle = (1 / aspect) * fovy;
		var ymin = -near * Math.tan(deg2rad(angle/2));
		// console.log('ymin = ' + ymin);
		var ymax = near * Math.tan(deg2rad(angle/2));
		console.log('ymax = ' + ymax);
        return new Mat4(
			2*near/(xmax-xmin), 0, (xmax+xmin)/(xmax-xmin), 0,
			0, 2*near/(ymax-ymin), (ymax+ymin)/(ymax-ymin), 0,
			0, 0, (far+near)/(near-far), 2*(far*near)/(near-far),
			0, 0, -1, 0
		).transpose();
		
		/* let f = 1.0 / Math.tan(fovy/2);
		let nf = 1 / (near - far);
		
		return new Mat4(
			f / aspect, 0, 0, 0,
			0, f, 0, 0,
			0, 0, (far+near) * nf, 0,
			0, 0, 2*far*near*nf, 0
		); */
    }

    /**
     * Creates a projection matrix using orthographic projection
     * 
     * @param {Number} left The left extent of the camera frustum (negative)
     * @param {Number} right The right extent of the camera frustum (positive)
     * @param {Number} aspect Aspect ratio of the canvas (width / height)
     * @param {Number} near Near plane distance
     * @param {Number} far Far plane distance
     * @returns {Mat4} An orthographic projection matrix
     */
    static projectionOrthographic( left, right, aspect, near, far ) {
		var ymin = -((right-left) * (1/aspect))/2;
		var ymax = ((right-left) * (1/aspect))/2;
        return new Mat4(
			2/(right-left), 0, 0, -(right+left)/(right-left),
			0, 2/(ymax-ymin), 0, -(ymax+ymin)/(ymax-ymin),
			0, 0, -2/(far-near), -(far+near)/(far-near),
			0, 0, 0, 1
		);
    }

    /**
     * Constructs a new 4x4 matrix 
     * Arguments are given in row-major order
     * They are stored in this.m in column-major order
     * 
     */
    constructor(m00, m01, m02, m03,
                m10, m11, m12, m13,
                m20, m21, m22, m23,
                m30, m31, m32, m33) {

        // store in column major format
        this.m = [
            [m00, m10, m20, m30],
            [m01, m11, m21, m31],
            [m02, m12, m22, m32],
            [m03, m13, m23, m33]
        ]
    }

    /**
     * Flattens the matrix for use with WebGL/OpenGL calls
     * 
     * @returns {Array<Number>} A linear list of matrix values in column-major order
     */
    flatten() {
        return [...this.m[0], ...this.m[1], ...this.m[2], ...this.m[3]]
    }

    /**
     * Performs column-major matrix multiplication of the current matrix and 'other'
     * 
     * @param {Mat4} other The matrix to multiply with
     * @returns {Mat4} The resulting matrix
     */
	multiply( other ) {
		var mult = new Mat4();
		// console.log(this.m[0].length + " | " + other.length);
        for (var i = 0; i < this.m.length; i++){
			for (var j = 0; j < other.m[0].length; j++){
				// console.log(i + " " + j);
				mult.m[i][j] = 0;
				for (var k = 0; k < this.m[0].length; k++){
					mult.m[i][j] += this.m[k][i] * other.m[j][k];
				}
				// console.log(mult.m[i][j]);
			}
		}
		
		return mult.transpose();
    }
	
	static dot(vector1, vector2) {
		return vector1[0] * vector2[0] + vector1[1] * vector2[1] + vector1[2] * vector2[2] + vector1[3] * vector2[3]
	}
	
	print(){
		var mText = '';
		for (var i = 0; i < this.m.length; i++){
			for (var j = 0; j < this.m[i].length; j++){
				mText += this.m[i][j] + '\t';
			}
			
			console.log(mText);
			mText = '';
		}
	}
	
	transpose(){
		var t = Mat4.identity();
		// console.log('initial transpose');
		// print(t);
		for (var i = 0; i < this.m.length; i++){
			for (var j = 0; j < this.m.length; j++){
				t.m[j][i] = this.m[i][j];
			}
		}
		
		return t;
	}
}


/**
 * @Class
 * WebGlApp that will call basic GL functions, manage a list of shapes, and take care of rendering them
 * 
 * This class will use the Shapes that you have implemented to store and render them
 */
class WebGlApp 
{
    /**
     * Initializes the app with a box, and the model, view, and projection matrices
     * 
     * @param {WebGL2RenderingContext} gl The webgl2 rendering context
     * @param {Shader} shader The shader to be used to draw the object
     */
    constructor( gl, shader )
    {
        // Store the shader
        this.shader = shader
        
        // Create a box instance and create a variable to track its rotation
        this.box = new Box( gl, shader, hex2rgb('#FFBF00') )
        this.box_animation_step = 0

        // Create the model matrix
        // Use an identity matrix initially
        this.model = new Mat4(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
		// this.model.print();

        // Create the view matrix
        // Point the camera at the origin and move it off-center
        this.eye     =   [2.5, 1.5, -2.5]
        this.center  =   [0, 0, 0]
        this.up      =   [0, 1, 0]
        this.view = Mat4.lookat(this.eye, this.center, this.up);
		// console.log('view');
		// this.view.print()

        // Create the projection matrix
        // Use a perspective projection initially
        this.fovy = 90
        this.aspect = 16/9
        this.left = -5
        this.right = 5
        this.near = 0.001
        this.far = 1000.0
        this.projection = Mat4.projectionPerspective(this.fovy, this.aspect, this.near, this.far).transpose();
		// console.log('projection')
		// this.projection.print()

        // Combine model, view and projection matrix into a single MVP
        // Pay attention to the correct order of multiplication
        this.mvp = this.projection.multiply(this.view).multiply(this.model);
		// console.log('mvp');
        // this.mvp.print();

		this.prev_animation = 'Rotate';
    }  

    /**
     * Sets the viewport of the canvas to fill the whole available space so we draw to the whole canvas
     * 
     * @param {WebGL2RenderingContext} gl The webgl2 rendering context
     * @param {Number} width 
     * @param {Number} height 
     */
    setViewport( gl, width, height )
    {
        gl.viewport( 0, 0, width, height )
    }

    /**
     * Clears the canvas color
     * 
     * @param {WebGL2RenderingContext} gl The webgl2 rendering context
     */
    clearCanvas( gl )
    {
        gl.clearColor(...hex2rgb('#000000'), 1.0)
        gl.clear(gl.COLOR_BUFFER_BIT)
    }
    
    /**
     * Updates components of this app
     * 
     * @param { AppState } app_state The state of the UI
     */
    update( app_state ) 
    {
        // This creates values between -1.0 and 1.0 that change continuously with time
        // Use this.box_animation_step to redefine the model matrix and realize the animation
        this.box_animation_step = (Math.sin(Date.now() / 2000))
		// console.log(this.box_animation_step)
        
        // Query the UI for Animation and set appropriate model matrix
		var animation = app_state.getState('Animation');
		if (animation !== this.prev_animation){
			this.model = Mat4.identity();
			this.prev_animation = animation;
			console.log('animation type changed');
		}
		if (animation === 'Translate'){
			this.model = Mat4.translation(this.box_animation_step / 100, 0, this.box_animation_step / 100).multiply(this.model);
			// this.model.print()
		} else if (animation === 'Scale'){
			this.model = Mat4.scale(this.box_animation_step / 200 + 1).multiply(this.model);
		} else if (animation === 'Rotate'){
			this.model = Mat4.rotationz(this.box_animation_step).multiply(this.model);
		}

        // Query the UI for Projection and set appropriate projection matrix
        var projection = app_state.getState('Projection');
		if (projection === 'Perspective'){
			this.projection = Mat4.projectionPerspective(this.fovy, this.aspect, this.near, this.far).transpose();
		} else if (projection === 'Orthographic'){
			this.projection = Mat4.projectionOrthographic(this.left, this.right, this.aspect, this.near, this.far);
		}

        // Extra Credit: Can you move the camera using the arrow keys on the keyboard

        // Re-compute the MVP matrix
        this.mvp = this.projection.multiply(this.view).multiply(this.model);
    }

    /**
     * Main render loop which sets up the active viewport (i.e. the area of the canvas we draw to)
     * clears the canvas with a background color and draws a box
     * 
     * @param {WebGL2RenderingContext} gl The webgl2 rendering context
     * @param {Number} canvas_width The canvas width. Needed to set the viewport
     * @param {Number} canvas_height The canvas height. Needed to set the viewport
     */
    render( gl, canvas_width, canvas_height )
    {
        // Set viewport and clear canvas
        this.setViewport( gl, canvas_width, canvas_height )
        this.clearCanvas( gl )

        // Activate the shader
        this.shader.use( )

        // Pass the MVP to the shader 
        // Use the shader's setUniform4x4f function to pass a 4x4 matrix
        // Use 'u_mvp' to find the MVP variable in the shader
        // Use Mat4's flatten() function to serialize the matrix for WebGL
		
		// this.mvp.print()
        this.shader.setUniform4x4f('u_mvp', this.mvp.flatten());
        // Render the box
        // This will use the MVP that was passed to the shader
        this.box.render( gl )
    }
}


// JS Module Export -- No need to modify this
export
{
    WebGlApp
}