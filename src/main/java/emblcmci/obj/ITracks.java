package emblcmci.obj;

import java.util.Collection;

public interface ITracks extends IBioObj{
	
	public Collection<?> values();

	public Object get(int trackID);

}
