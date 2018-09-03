#!/usr/bin/env bash
export WHYNOT_SETTINGS='default_settings.py'
gunicorn -k gevent -b 127.0.0.1:15000 whynot_web.application:app
