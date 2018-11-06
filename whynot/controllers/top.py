# A data structure that remembers the N highest objects added
class top_highest (object):

    def __init__ (self, size):

        self.size = size
        self.d = {}
        self.order = []

    def add (self, rank, obj):

        if len (self.order) >= self.size and rank < self.order [0]:
            return # no place in list

        # Place the rank number in the ordered list
        i = 0
        while i < len (self.order):

            if rank < self.order [i]:
                self.order.insert (i, rank)
                break

            i += 1

        if i >= len (self.order): # larger than any of them
            self.order.append (rank)

        self.d [rank] = obj

        if len (self.order) > self.size:
            self.order = self.order [-self.size:]

    def get (self):

        l = []
        for k in self.order:
            l.append (self.d [k])

        return l
