package nl.ru.cmbi.whynot;

import org.apache.wicket.Page;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;

import nl.ru.cmbi.whynot.error.MyExceptionErrorPage;

public class MyRequestCycle extends WebRequestCycle {
	public MyRequestCycle(WebApplication application, WebRequest request, Response response) {
		super(application, request, response);
	}

	@Override
	public Page onRuntimeException(Page page, RuntimeException e) {
		return new MyExceptionErrorPage(page, e);
	}
}
