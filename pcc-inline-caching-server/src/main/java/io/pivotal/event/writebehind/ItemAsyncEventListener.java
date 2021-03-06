package io.pivotal.event.writebehind;

import java.util.List;
import java.util.Properties;

import org.apache.geode.LogWriter;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Operation;
import org.apache.geode.cache.asyncqueue.AsyncEvent;
import org.apache.geode.cache.asyncqueue.AsyncEventListener;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

import io.pivotal.util.MongoConnection;

@SuppressWarnings("deprecation")
public class ItemAsyncEventListener implements AsyncEventListener {

	private static LogWriter log;

	static {
		log = CacheFactory.getAnyInstance().getDistributedSystem().getLogWriter();
	}

	public void init(Properties props) {}

	public void close() {}

	@SuppressWarnings({ "rawtypes"})
	@Override
	public boolean processEvents(List<AsyncEvent> events) {

		for (AsyncEvent ge : events) {
			if (ge.getOperation().equals(Operation.CREATE)) {
				PdxInstance item = (PdxInstance) ge.getDeserializedValue();

				log.info("CREATE event caught... Inserting into MongoDB now...");
				
				insertItem(JSONFormatter.toJSON(item));
			}
			// SKIP "UPDATE" AND "DELETE" EVENT FOR NOW
		}
		
		return true;
	}
	
	private void insertItem(String jsonString) {
		DBCollection itemCollection = MongoConnection.getInstance().itemCollection;
		
		BasicDBObject doc = new BasicDBObject().parse(jsonString);
		
		itemCollection.insert(doc); 
	}
	
}
