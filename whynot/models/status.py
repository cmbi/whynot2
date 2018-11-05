

class Status:
    @staticmethod
    def from_string(value):
        value = value.upper()
        for status in STATUSES:
            if value == str(status):
                return status

        raise ValueError("No such status: {}".format(value))

    def __init__(self, value):
        self.value = value.upper()

    def __repr__(self):
        return self.value


PRESENT = Status('PRESENT')
VALID = Status('VALID')
OBSOLETE = Status('OBSOLETE')
MISSING = Status('MISSING')
UNANNOTATED = Status('UNANNOTATED')
ANNOTATED = Status('ANNOTATED')

STATUSES = [PRESENT, VALID, OBSOLETE, MISSING, UNANNOTATED, ANNOTATED]
