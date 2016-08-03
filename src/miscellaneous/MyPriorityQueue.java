package miscellaneous;

import java.util.PriorityQueue;

public class MyPriorityQueue<E> extends PriorityQueue<E> 
{
	private static final long serialVersionUID = 1L;

	@Override
    public boolean offer(E e) 
    {
        boolean is_added = false;
        if(!super.contains(e))
        {
            is_added = super.offer(e);
        }
        return is_added;
    }
}

