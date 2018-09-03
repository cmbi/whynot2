# Requirements
* a mongodb server: https://www.mongodb.com/
* python pip: https://pypi.org/project/pip/
* cmbi databanks installed locally: https://github.com/cmbi/databanks.git


# installing
1. run: pip install -r requirements:
2. set the passwords and hostnames for the database in "whynot_web/default_settings.py" and "update_settings.py"
   call 'storage.authenticate' too if the database is password protected.
3. run install.py


# Usage
Use ./crawl.py <DATABANK> <FILE or DIRECTORY> to update the database with new entries.
Use ./annotate.py to check for missing entries and annotate a reason why it is missing.
Run ./run_site.sh to run the whynot website.
