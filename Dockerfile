FROM python:2.7-onbuild

WORKDIR /usr/src/app

EXPOSE 15000
CMD ["gunicorn", "-k", "gevent", "-b", "0.0.0.0:15000", "whynot_web.application:app"]
