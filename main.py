from flask import Flask
from flask import render_template, request, redirect, session, url_for, escape, make_response, flash, abort
import threading

app = Flask(__name__)
# (session encryption) keep this really secret:
app.secret_key = "bnNoqxXSgzoXSOezxpZjb8mrMp5L0L4mJ4o8nRzn"

@app.route('/')
def index():
	return render_template('index.html')

@app.route('/get_status')
def get_status():
	return 

from flask_socketio import SocketIO, send, emit
socketio = SocketIO(app)

@app.before_first_request
def switch_light():
	threading.Timer(5.0, switch_light).start()
	socketio.emit('light_switched', {})

# -------------------------------------------------------------------

if __name__ == '__main__':
	#host='0.0.0.0' only with debug disabled - security risk
	#app.run(port=8080, debug=True) - don't use this one with sockets
	socketio.run(app, port=8080, debug=True) # only use this with sockets