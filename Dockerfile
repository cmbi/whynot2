FROM python:3.8

# Requirements
RUN apt-get update
RUN apt-get install -y wget g++ make autoconf automake autotools-dev libbz2-dev libboost-all-dev git
RUN mkdir -p /deps /usr/local/bin

# libzeep
RUN git clone https://github.com/mhekkel/libzeep.git /deps/libzeep ;\
    cd /deps/libzeep ;\
    git checkout tags/v3.0.3
# Workaround due to bug in libzeep's makefile
RUN sed -i '71s/.*/\t\$\(CXX\) \-shared \-o \$@ \-Wl,\-soname=\$\(SO_NAME\) \$\(OBJECTS\) \$\(LDFLAGS\)/' /deps/libzeep/makefile
WORKDIR /deps/libzeep
# Run ldconfig manually to work around a bug in libzeep's makefile
RUN make -j ; make install ; ldconfig

# mkdssp
RUN git clone https://github.com/cmbi/dssp.git /deps/dssp
WORKDIR /deps/dssp
RUN git checkout tags/3.1.4
RUN ./autogen.sh && ./configure && make -j && make install

# mkhssp
RUN git clone https://github.com/cmbi/hssp.git /deps/hssp
WORKDIR /deps/hssp
RUN git checkout tags/3.1.5
RUN ./autogen.sh && ./configure && make -j && make install

# whynot
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY requirements /usr/src/app/
RUN pip install --no-cache-dir -r requirements
COPY . /usr/src/app

# settings
EXPOSE 15000
