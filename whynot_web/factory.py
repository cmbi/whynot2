import logging
from logging.handlers import SMTPHandler

from flask import Flask

from storage import storage

_log = logging.getLogger(__name__)


def create_app(settings=None):
    _log.info("Creating app")

    app = Flask(__name__, static_folder='frontend/static',
                template_folder='frontend/templates')

    app.config.from_object('whynot_web.default_settings')
    if settings:
        app.config.update(settings)
    else:  # pragma: no cover
        app.config.from_envvar('WHYNOT_SETTINGS')  # pragma: no cover

    # Ignore Flask's built-in logging
    # app.logger is accessed here so Flask tries to create it
    app.logger_name = "nowhere"
    app.logger

    # Configure email logging. It is somewhat dubious to get _log from the
    # root package, but I can't see a better way. Having the email handler
    # configured at the root means all child loggers inherit it.
    from whynot_web import _log as root_logger

    if not app.debug and not app.testing:  # pragma: no cover
        # Only log to email during production.
        mail_handler = SMTPHandler((app.config["MAIL_SERVER"],
                                    app.config["MAIL_SMTP_PORT"]),
                                    app.config["MAIL_FROM"],
                                    app.config["MAIL_TO"],
                                    "pdb catalog failed")
        mail_handler.setLevel(logging.ERROR)
        root_logger.addHandler(mail_handler)
        mail_handler.setFormatter(
            logging.Formatter("Message type: %(levelname)s\n" +
                              "Location: %(pathname)s:%(lineno)d\n" +
                              "Module: %(module)s\n" +
                              "Function: %(funcName)s\n" +
                              "Time: %(asctime)s\n" +
                              "Message:\n" +
                              "%(message)s"))
    elif not app.testing:
        # Only log to the console during development and production, but not
        # during testing.
        ch = logging.StreamHandler()
        formatter = logging.Formatter(
            '%(asctime)s - %(levelname)s - %(message)s')
        ch.setFormatter(formatter)
        root_logger.addHandler(ch)

        root_logger.setLevel(logging.DEBUG)
    else:
        root_logger.setLevel(logging.DEBUG)

    # Use ProxyFix to correct URL's when redirecting.
    from whynot_web.middleware import ReverseProxied
    app.wsgi_app = ReverseProxied(app.wsgi_app)

    # Initialise extensions
    from whynot_web import toolbar
    toolbar.init_app(app)

    # Register blueprints
    from whynot_web.frontend.dashboard.views import bp as dashboard_bp
    from whynot_web.frontend.dashboard.rs import bp as rs_bp
    app.register_blueprint (dashboard_bp)
    app.register_blueprint (rs_bp)

    return app
