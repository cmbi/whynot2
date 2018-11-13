import re

p_tag_enclosed = re.compile("\\<(\\w+)(\\s+.+?|\\s+\".+?\")*\\>(.*)\\<\\/\\1\\>")
p_single_tag = re.compile("\\<\\w+(\\s+\\w+\\=.+)*\\/\\>")

# Removes xml tags from text, some comments have them.
def remove_tags (s):

    while True:

        m = p_tag_enclosed.search (s)
        if m:
            s = s[:m.start()] + m.group (3) + s [m.end():]
        else:
            break

    while True:

        m = p_single_tag.search (s)
        if m:
            s = s[:m.start()] + s [m.end():]
        else:
            break

    return s

class comment_node (object):

    def __init__(self, title):

        self.title = title
        self.entries = []
        self.subtree = {}

    def list_entries (self):

        entries = self.entries
        for child in self.subtree.values ():
            entries.extend (child.list_entries())

        return entries

def build_tree (root_string, comments_entries_dict):

    tree = {}

    prefix = root_string + ':'

    for key in comments_entries_dict:

        full_text = remove_tags (key)

        if full_text.startswith (prefix):

            i = full_text.find (':', len (prefix))
            if i == -1:
                # No further subdivision possible
                root_text = full_text
            else:
                root_text = full_text [:i]

            if root_text not in tree:

                title = root_text
                if root_text == full_text:
                    title = key # with tags

                tree [root_text] = comment_node (title)
                tree [root_text].subtree = build_tree (title, comments_entries_dict)

                if len (tree [root_text].subtree) <= 0:
                    tree [root_text].entries = comments_entries_dict [key]

    return tree

def remove_unbranched_comment_nodes (tree):

    for key in tree:

        if len (tree [key].subtree) == 1:

            subtree = tree [key].subtree
            tree.pop (key)
            key = list(subtree)[0]
            tree [key] = subtree [key]

        tree [key].subtree = remove_unbranched_comment_nodes (tree [key].subtree)

    return tree

# The comments_to_tree function builds a hierarchical data structure for the given comments.
# Here, comments are split up in nodes, based on the colon characters they contain.
# For example: the comments "Experimental method: SOLUTION NMR" and
# "Experimental method: ELECTRON MICROSCOPY" both have the same parent,
# namely "Experimental method"
def comments_to_tree (comments_entries_dict):

    tree = {}

    # keys are comments, values are entries
    for key in comments_entries_dict:

        full_text = remove_tags (key) # don't count colons in xml tags

        if ':' in full_text:

            prfx = full_text [:full_text.find (':')]
            tree [prfx] = comment_node (prfx)
            tree [prfx].subtree = build_tree (prfx, comments_entries_dict)

        else:
            tree [key] = comment_node (key)
            tree [key].entries = comments_entries_dict [key]

    return remove_unbranched_comment_nodes (tree)
