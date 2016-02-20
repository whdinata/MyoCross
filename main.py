from flask import Flask
from flask import render_template, request, redirect, session, url_for, escape, make_response, flash, abort
import threading
import json

app = Flask(__name__)
# (session encryption) keep this really secret:
app.secret_key = "bnNoqxXSgzoXSOezxpZjb8mrMp5L0L4mJ4o8nRzn"

from flask_socketio import SocketIO, send, emit
socketio = SocketIO(app)

light_state = 0
my_thread = threading.Thread()

@app.route('/')
def index():
	return render_template('index.html', light_state=light_state)

@app.route('/get_status')
def get_status():
	col = "amber"
	if(light_state == 0):
		col = "red"
	elif(light_state == 2):
		col = "green"

	return json.dumps({"state":light_state})

@app.route('/press_button', methods=['POST'])
def press_button():
	print("button pressed")
	global light_state
	global my_thread
	light_state = 2 # green
	# restart timer
	my_thread.cancel()
	my_thread = threading.Timer(5.0, switch_light)
	my_thread.start()
	socketio.emit('button_pressed', {"data":light_state})

# ordinary function
def switch_light(): 
	print("switching")
	global light_state
	global my_thread
	light_state = (light_state+1)%4 # change global variable
	my_thread = threading.Timer(5.0, switch_light) # schedule next switch
	my_thread.start()
	socketio.emit('light_switched', {"data":light_state})

@app.route('/debug')
def debug():
	press_button()
	return "ok"

# -------------------------------------------------------------------

if __name__ == '__main__':
	#host='0.0.0.0' only with debug disabled - security risk
	switch_light()
	socketio.run(app, port=8080, debug=True)
	