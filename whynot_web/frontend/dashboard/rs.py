from flask import Blueprint

bp = Blueprint('rest', __name__, url_prefix='/webservice/rs')

@bp.route ('/')
def docs ():
    # TODO: documentation here
    pass

@bp.route ('/annotations/<databank_name>/<pdbid>/')
def annotations (databank_name, pdbid):
    pass
    # TODO: output

@bp.route ('/entries/<databank_name>/<collection>/')
def entries (databank_name, collection):
    pass
    # TODO: output

