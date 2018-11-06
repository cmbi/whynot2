

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

    def is_present(self):
        return self.value in ['PRESENT', 'VALID', 'OBSOLETE']

    def is_missing(self):
        return self.value in ['MISSING', 'UNANNOTATED', 'ANNOTATED']

    def is_obsolete(self):
        return self.value == 'OBSOLETE'

    def is_valid(self):
        return self.value == 'VALID'

    def is_unannotated(self):
        return self.value == 'UNANNOTATED'

    def is_annotated(self):
        return self.value == 'ANNOTATED'


PRESENT = Status('PRESENT')
VALID = Status('VALID')
OBSOLETE = Status('OBSOLETE')
MISSING = Status('MISSING')
UNANNOTATED = Status('UNANNOTATED')
ANNOTATED = Status('ANNOTATED')

STATUSES = [PRESENT, VALID, OBSOLETE, MISSING, UNANNOTATED, ANNOTATED]
