import base64
import hashlib
import hmac
import pickle
import re

from flask import request, Response, jsonify

from app import app

in_class=open('pickle_class','rb')
clf_class=pickle.load(in_class)
in_class.close()
# print(clf_class)

in_reg=open('pickle_reg','rb')
clf_reg=pickle.load(in_reg)
in_reg.close()
# print(clf_reg)

print("\ndeserialized machine learning structures\n")

@app.route('/api/<string:user>/comments_prediction', methods=['POST'])
def predictCommentsScore(user):
    if not is_authorized(request.data, request.headers.get('Auth', type=str),user):
        return Response(status=401)

    return jsonify(handleRequestJSON(request.json))

@app.route('/api/<string:user>/comments_prediction/<int:score>', methods=['POST'])
def predictCommentsScoreWithMinimumValue(user,score):
    if not is_authorized(request.data, request.headers.get('Auth', type=str), user):
        return Response(status=401)

    return jsonify(handleRequestJSON(request.json,lambda c : c >= score))

print("routes included\n")

def handleRequestJSON(requestJSON,condition=lambda score : True):
    print('REQUEST: ',requestJSON)
    response = {}

    for key, comment in requestJSON.items():
        parsed_comment=parse_body(str(comment))
        response[key] = {'score': int(clf_reg.predict([parsed_comment])[0]), 'shouldBeRemoved':bool(clf_class.predict([parsed_comment])[0])}

    print('RESPONSE: ',response)
    return response

def is_authorized(request_data, hmac_value,user_name):
    def getSecret(userName):
        keyMap={"yjqc2tbi9b": "NTA2NTgzMTMwNA=="}
        if userName in keyMap:
            print('found one')
            return keyMap[userName]
        else:
            return ""
    def getMessageHash(data, secret):
        digestedMesage = hmac.new(
            base64.standard_b64decode(secret),
            msg=data,
            digestmod=hashlib.sha256
        ).digest()
        return base64.standard_b64encode(digestedMesage);

    actual_hmac = getMessageHash(request_data, getSecret(user_name)).decode()
    print('PYTHON: ' + actual_hmac, '\n', 'JAVA: ' + hmac_value)
    return actual_hmac == hmac_value

def parse_body(body, token_value=' li_nk'):
    temp = re.sub('\\(?((http(s)?://)|(www))(.)*?([ \\t\\n]|$)\\)?', token_value + '\\6', body)
    return re.sub('&(.*?);', '', temp)
