FROM python:3.5

RUN apt-get update && apt-get install -y make libboost-all-dev autoconf automake autotools-dev

# mkdssp
RUN git clone https://github.com/cmbi/xssp.git /deps/xssp ;\
    cd /deps/xssp ;\
    git checkout tags/3.0.5
WORKDIR /deps/xssp
RUN aclocal ; autoheader ; automake --add-missing ; autoconf
RUN ./configure && make -j mkdssp && install -m755 mkdssp /usr/local/bin/mkdssp

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY requirements /usr/src/app/
RUN pip install --no-cache-dir -r requirements
COPY . /usr/src/app

EXPOSE 15000
