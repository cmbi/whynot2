java -cp dependency/*:whynot-apps-2.0-090612.jar nl.ru.cmbi.whynot.backup.Backuper;
tar -cf backup/$(date +%y%m%d).tar backup/*.backup;
