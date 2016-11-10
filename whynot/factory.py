import logging
from logging.handlers import SMTPHandler

from flask import Flask


_log = logging.getLogger(__name__)
sh = logging.StreamHandler()


def create_app(settings=None):
    _log.info("Creating app")

    app = Flask(__name__, static_folder='frontend/static',
                template_folder='frontend/templates')

    app.config.from_object('whynot.default_settings')
    if settings:
        app.config.update(settings)
    else:  # pragma: no cover
        app.config.from_envvar('WHYNOT_SETTINGS')  # pragma: no cover

    # Ignore Flask's built-in logging
    # app.logger is accessed here so Flask tries to create it
    app.logger_name = "nowhere"
    app.logger

    whynot_logger = logging.getLogger('whynot')

    # Only log to email during production.
    if not app.debug and not app.testing:  # pragma: no cover
        mail_handler = SMTPHandler((app.config["MAIL_SERVER"],
                                   app.config["MAIL_SMTP_PORT"]),
                                   app.config["MAIL_FROM"],
                                   app.config["MAIL_TO"],
                                   "whynot failed")
        mail_handler.setLevel(logging.ERROR)
        whynot_logger.addHandler(mail_handler)
        mail_handler.setFormatter(
            logging.Formatter("Message type: %(levelname)s\n" +
                              "Location: %(pathname)s:%(lineno)d\n" +
                              "Module: %(module)s\n" +
                              "Function: %(funcName)s\n" +
                              "Time: %(asctime)s\n" +
                              "Message:\n" +
                              "%(message)s"))

    # Only log to the console during development and production, but not during
    # testing.
    if app.testing:
        whynot_logger.setLevel(logging.DEBUG)
    else:
        # This is the formatter used for the Flask app.
        formatter = logging.Formatter(
            '%(asctime)s - %(name)s - %(levelname)s - %(message)s')
        sh.setFormatter(formatter)
        whynot_logger.addHandler(sh)

        if app.debug:
            whynot_logger.setLevel(logging.DEBUG)
        else:
            whynot_logger.setLevel(logging.INFO)

    # Configure storage
    from storage import storage
    storage.uri = app.config['MONGODB_URI']
    storage.db_name = app.config['MONGODB_DB_NAME']
    storage.connect()

    # Setup the default databanks if there are none
    if storage.count('databanks', {}) == 0:
        from install import create_databanks
        storage.create_index('databanks', 'name')
        storage.create_index('entries', 'databank_name')
        storage.create_index('entries', 'pdbid')
        storage.create_index('entries', 'comment')
        databanks = create_databanks()
        storage.insert('databanks', databanks)

    # Use ProxyFix to correct URL's when redirecting.
    from whynot.middleware import ReverseProxied
    app.wsgi_app = ReverseProxied(app.wsgi_app)

    # Initialise extensions
    from whynot import toolbar
    toolbar.init_app(app)

    # Register jinja2 filters
    from whynot.frontend.filters import beautify_docstring
    app.jinja_env.filters['beautify_docstring'] = beautify_docstring

    # Register blueprints
    from whynot.frontend.dashboard.views import bp as dashboard_bp
    from whynot.frontend.rest.rs import bp as rs_bp
    app.register_blueprint(dashboard_bp)
    app.register_blueprint(rs_bp)

    return app
