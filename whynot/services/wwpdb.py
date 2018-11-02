from urllib.request import urlopen

from whynot.settings import settings


def get_content_types():
    content_types = {}

    r = urlopen(settings['WWPDB_TYPE_URL'])
    for line in r:
        if len(line.strip()) > 0:
            pdbid, content, method = line.decode('ascii').split()

            content_types[pdbid.lower()] = content

    return content_types


def get_method_types():
    method_types = {}

    r = urlopen(settings['WWPDB_TYPE_URL'])
    for line in r:
        if len(line.strip()) > 0:
            pdbid, content, method = line.decode('ascii').split()

            method_types[pdbid.lower()] = method

    return method_types

