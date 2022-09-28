import { hex2rgb } from './js/utils/utils.js'

/**
 * Initializes WebGL2 
 * @returns { WebGL2RenderingContext | null } The WebGL2 context or Null
 */
function initGl( )
{
	console.log("running initGl");
	const canvas = document.getElementById('canvas');
	
	if (!canvas){
		console.log('no html5 canvas found');
	}
	
	const gl = canvas.getContext('webgl2');
	
	if (gl){
		console.log('WebGL set up successfully');
		return gl;
	} else {
		console.log('WebGL not supported');
		return null;
	}
	
	gl.clearColor(0.0, 0.5, 0.0, 1.0);
	gl.clear(gl.COLOR_BUFFER_BIT);
    //throw '"initGl" not implemented' 

}


/**
 * Clears scene and canvas
 * 
 * Find the Aggie colors here: https://marketingtoolbox.ucdavis.edu/brand-guide/colors
 * Use the 'hex2rgb' function to convert HEX colors
 * 
 * Use app_state.getState( ui_element_name ) to query the UI state
 * For example app_state.getState( 'Canvas Color' ) returns the currently selected color name
 * 
 * @param { WebGL2RenderingContext } gl The WebGL2 context used on the canvas element
 * @param { AppState } app_state The state of the UI
 */
function clearCanvas( gl, app_state )
{
	if (!gl){
		console.log('gl not working in clearCanvas');
	}
	window.onload = initGl;
	console.log('onload run');
    // throw '"clearCanvas" not implemented' 
	switch (app_state.getState('Canvas Color')){
		case 'Aggie Blue': {
			var rgb = hex2rgb("022851");
			gl.clearColor(rgb[0], rgb[1], rgb[2], 1.0);
			gl.clear(gl.COLOR_BUFFER_BIT);
			break;
		}
		
		case 'Aggie Gold': {
			var rgb = hex2rgb("FFBF00");
			gl.clearColor(rgb[0], rgb[1], rgb[2], 1.0);
			gl.clear(gl.COLOR_BUFFER_BIT);
			break;
		}
			
	}
}


// JS Module Export -- No need to modify this
export
{

    initGl,
    clearCanvas,

}