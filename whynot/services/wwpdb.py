class WwPdb:
    def __init__(self, url=None):
        self._url = url

    @property
    def url(self):
        return self._url

    @url.setter
    def url(self, url):
        self._url = url

    def get(self):
        """
        Parses the wwpdb file into a dict.
        """
        wwpdb_file = self._download_file(self.url)
        if not os.path.exists(wwpdb_file):
            raise Exception("%s not found" % wwpdb_file)

        wwpdb_data = []
        with open(wwpdb_file, 'r') as f:
            for line in f:
                if len(line.strip()) <= 0:
                    continue

                pdb_id, c_type, method = line.split()
                wwpdb_data.append({
                    'pdb_id': pdb_id,
                    'c_type': c_type,
                    'method': method
                })
        return wwpdb_data

    # TODO: Cache this with dogpile. Check every month?
    # TODO: Put this code in helper module
    def _download_file(url):
        local_filename = os.path.join('/tmp', url.split('/')[-1])
        r = requests.get(url, stream=True)
        with open(local_filename, 'wb') as f:
            for chunk in r.iter_content(chunk_size=1024):
                if chunk: # filter out keep-alive new chunks
                    f.write(chunk)
        return local_filename


wwpdb = WwPdb()
