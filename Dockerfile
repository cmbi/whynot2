FROM python:2.7

RUN mkdir -p /app
WORKDIR /app

COPY . /app
RUN pip install -r requirements

EXPOSE 15000
ENV WHYNOT_SETTINGS /app/dev_settings.py

CMD ["gunicorn", "-k", "gevent", "-b", "0.0.0.0:15000", "whynot_web.application:app"]
