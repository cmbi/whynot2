package nl.ru.cmbi.whynot.hibernate;

import nl.ru.cmbi.whynot.model.Comment;

public interface CommentRepo extends DomainObjectRepository<Comment> {
	Comment findByText(final String text);
}
