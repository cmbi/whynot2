def parse_whynot(path):
    comment = None
    comments = {}
    with open(path, 'r') as f:
        for line in f:
            if line.startswith("COMMENT:"):
                comment = line[8:].strip()
                if comment not in comments:
                    comments[comment] = []
            elif len(line.strip()) > 0:
                db, pdbid = line.strip().split(',')
                comments[comment].append((db, pdbid))
    return comments
