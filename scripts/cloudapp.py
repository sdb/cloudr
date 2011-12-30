from bottle import route, run, request, HTTPError
from json import dumps
import threading

user_counter = 1
item_counter = 1

class User():
    def __init__(self, em, pwd, items = []):
        global user_counter
        self.id = user_counter
        self.email = em
        self.password = pwd
        self.items = items
        user_counter += 1

class Item():
    def __init__(self):
        global item_counter
        self.id = item_counter
        item_counter += 1

users = [
    User('stefan@ellefant.be','teste'),
    User('1@1.be','1', [Item()])
]

class Context(threading.local):
    def __init__(self):
        self.user = None

context = Context()

class auth(object):

    def __init__(self, f):
        self.f = f

    def __call__(self, *args):
        if not request.auth:
			raise HTTPError(401, header={'WWW-Authenticate': 'Basic realm="realm"'})
        else:
            usr = login(request.auth[0], request.auth[1])
            if usr == None:
                raise HTTPError(401)
            else:
                context.user = usr
                return self.f(*args)

def login(em, pwd):
	def authenticated(usr):
		return usr.email == em and usr.password == pwd
	auth = filter(authenticated, users)
	if len(auth) == 0:
		return None
	else:
		return auth[0]


@route('/items')
@auth
def items():
    return dumps([i.__dict__ for i in context.user.items])

@route('/account')
@auth
def account():
    return {'id':context.user.id,'email':context.user.email}

run(host='localhost', port=8082, reloader=True)