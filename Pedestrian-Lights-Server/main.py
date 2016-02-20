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
my_thread = None
green_duration = 10

@app.route('/')
def index():
	return render_template('index.html', light_state=light_state)

@app.route('/get_status')
def get_status():
	return json.dumps({"state": "green" if light_state == 1 else "red"})

@app.route('/press_button', methods=['POST'])
def press_button():
	print("button pressed")
	global light_state
	global my_thread
	
	# restart timer
	if(my_thread != None):
		my_thread.cancel()
	my_thread = threading.Timer(green_duration, switch_light)
	my_thread.start()
	light_state = 1 # green
	socketio.emit('button_pressed', {"data":light_state, "time":green_duration})


def switch_light(): 
	print("switching")
	global light_state
	light_state = 0 # red


@app.route('/debug')
def debug():
	press_button()
	return "ok"

# -------------------------------------------------------------------

if __name__ == '__main__':
	#host='0.0.0.0' only with debug disabled - security risk
	switch_light()
	socketio.run(app, port=8080, debug=True)
	