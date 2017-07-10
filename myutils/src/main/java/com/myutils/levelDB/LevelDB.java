package com.myutils.levelDB;

import static org.fusesource.leveldbjni.JniDBFactory.asString;
import static org.fusesource.leveldbjni.JniDBFactory.bytes;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationSubmissionContextPBImpl;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProto;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerRecoveryProtos.ApplicationStateDataProto;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore.RMState;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationAttemptStateData;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationStateData;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.impl.pb.ApplicationAttemptStateDataPBImpl;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.impl.pb.ApplicationStateDataPBImpl;
import org.apache.hadoop.yarn.server.utils.LeveldbIterator;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;

public class LevelDB {
	

	protected static final String RM_APP_ROOT = "RMAppRoot";
	private static final String SEPARATOR = "/";
	private static final String RM_APP_KEY_PREFIX =
		      RM_APP_ROOT + SEPARATOR + ApplicationId.appIdStrPrefix;

	public Charset charset = Charset.forName("utf-8");
	private DB db;
	
	public File dbFile = new File("C:\\Users/july/Desktop/test/db/leveldb-timeline-store.ldb");
//	public File dbFile = new File("/hadoop/yarn/timeline/test.ldb");
	
	


	
	public DB openDatabase() throws IOException {
		Options options = new Options();
		options.createIfMissing(false);
		try {
			db = JniDBFactory.factory.open(dbFile, options);
		} catch (IOException e) {
			e.printStackTrace();
			options.createIfMissing(true);
			try {
				db = JniDBFactory.factory.open(dbFile, options);
			} catch (DBException dbErr) {
				dbErr.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return db;
	}
	
	 public ApplicationStateData loadRMAppState(ApplicationId appId) throws IOException {
		    String appKey = getApplicationNodeKey(appId);
		    byte[] data = null;
		    try {
		      data = db.get(bytes(appKey));
		    } catch (DBException e) {
		      throw new IOException(e);
		    }
		    if (data == null) {
		      return null;
		    }
		    System.out.println("data ==>"+asString(data));
		    return createApplicationState(appId.toString(), data);
		  }
	 
	 private String getApplicationNodeKey(ApplicationId appId) {
		    return RM_APP_ROOT + SEPARATOR + appId;
		  }
	 
	 private ApplicationStateData createApplicationState(String appIdStr,
		      byte[] data) throws IOException {
		    ApplicationId appId = ConverterUtils.toApplicationId(appIdStr);
		    ApplicationStateDataPBImpl appState =
		        new ApplicationStateDataPBImpl(
		            ApplicationStateDataProto.parseFrom(data));
		    if (!appId.equals(
		        appState.getApplicationSubmissionContext().getApplicationId())) {
		      throw new YarnRuntimeException("The database entry for " + appId
		          + " contains data for "
		          + appState.getApplicationSubmissionContext().getApplicationId());
		    }
		    return appState;
		  }

	public void loadRMApps(RMState state) {
		int numApps = 0;
		int numAppAttempts = 0;
		LeveldbIterator iter = null;
		try {
			iter = new LeveldbIterator(db);
			iter.seek(bytes(RM_APP_KEY_PREFIX));
			while (iter.hasNext()) {
				Entry<byte[], byte[]> entry = iter.next();
				String key = new String(entry.getKey(),charset);
				numApps++;
				System.out.println(key+" ==>> "+new String(entry.getValue(),charset));
				try {
					Thread.sleep(1111);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!key.startsWith(RM_APP_KEY_PREFIX)) {
			          continue;
			        }
				
				 String appIdStr = key.substring(RM_APP_ROOT.length() + 1);
			        if (appIdStr.contains(SEPARATOR)) {
			          continue;
			        }
				
//				 numAppAttempts += loadRMApp(state, iter, appIdStr, entry.getValue());
			        ++numApps;
				
//				if (!key.startsWith("application_")) {
//					break;
//				}

			}
			System.out.println(numApps);
		} catch (DBException e) {
		} finally {
			if (iter != null) {
				try {
					iter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	 private int loadRMApp(RMState rmState, LeveldbIterator iter, String appIdStr,
		      byte[] appData) throws IOException {
		    ApplicationStateData appState = createApplicationState(appIdStr, appData);
		    ApplicationId appId =
		        appState.getApplicationSubmissionContext().getApplicationId();
		    rmState.getApplicationState().put(appId, appState);
		    String attemptNodePrefix = getApplicationNodeKey(appId) + SEPARATOR;
		    while (iter.hasNext()) {
		      Entry<byte[],byte[]> entry = iter.peekNext();
		      String key = asString(entry.getKey());
		      if (!key.startsWith(attemptNodePrefix)) {
		        break;
		      }

		      String attemptId = key.substring(attemptNodePrefix.length());
		      if (attemptId.startsWith(ApplicationAttemptId.appAttemptIdStrPrefix)) {
		        ApplicationAttemptStateData attemptState =
		            createAttemptState(attemptId, entry.getValue());
		        appState.attempts.put(attemptState.getAttemptId(), attemptState);
		      }
		      iter.next();
		    }
		    int numAttempts = appState.attempts.size();
		    return numAttempts;
		  }
	 
	 private ApplicationAttemptStateData createAttemptState(String itemName,
		      byte[] data) throws IOException {
		    ApplicationAttemptId attemptId =
		        ConverterUtils.toApplicationAttemptId(itemName);
		    ApplicationAttemptStateDataPBImpl attemptState =
		        new ApplicationAttemptStateDataPBImpl(
		            ApplicationAttemptStateDataProto.parseFrom(data));
		    if (!attemptId.equals(attemptState.getAttemptId())) {
		      throw new YarnRuntimeException("The database entry for " + attemptId
		          + " contains data for " + attemptState.getAttemptId());
		    }
		    return attemptState;
		  }
	 
	 protected void removeApplicationStateInternal(ApplicationStateData appState)
		      throws IOException {
		    ApplicationId appId =
		        appState.getApplicationSubmissionContext().getApplicationId();
		    String appKey = getApplicationNodeKey(appId);
		    try {
		      WriteBatch batch = db.createWriteBatch();
		      try {
		        batch.delete(bytes(appKey));
		        for (ApplicationAttemptId attemptId : appState.attempts.keySet()) {
		          String attemptKey = getApplicationAttemptNodeKey(appKey, attemptId);
		          batch.delete(bytes(attemptKey));
		        }
		        db.write(batch);
		      } finally {
		        batch.close();
		      }
		    } catch (DBException e) {
		      throw new IOException(e);
		    }
		  }
	 
	 private String getApplicationAttemptNodeKey(String appNodeKey,
		      ApplicationAttemptId attemptId) {
		    return appNodeKey + SEPARATOR + attemptId;
		  }
	
	  protected void storeApplicationStateInternal(ApplicationId appId,
		      ApplicationStateData appStateData) throws IOException {
		    String key = getApplicationNodeKey(appId);
		    try {
		      db.put(bytes(key), appStateData.getProto().toByteArray());
		    } catch (DBException e) {
		      throw new IOException(e);
		    }
		  }

	public static void main(String[] args) throws IOException {
		ApplicationId appId = ApplicationId.newInstance(Long.parseLong("1495617822405"), 4444);
		ApplicationStateData appStateData = ApplicationStateData.newInstance(Long.MIN_VALUE, Long.MIN_VALUE, new ApplicationSubmissionContextPBImpl(), "user");
		System.out.println("appId ==>"+appId);
		RMState rmState = new RMState();
		LevelDB levelDB = new LevelDB();
		levelDB.openDatabase();
		levelDB.loadRMApps(rmState);
//		System.out.println(rmState.getApplicationState());
//		ApplicationStateData app = levelDB.loadRMAppState(appId);
//		System.out.println(app);
//		LeveldbRMStateStore db = new LeveldbRMStateStore();
//		try {
//			db.loadState();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
//		levelDB.storeApplicationStateInternal(appId, appStateData);
		
//		levelDB.db.put(bytes("ouou"), bytes("qiqi"));
		
//		String str = asString(levelDB.db.get(bytes("eYARN_APPLICATION"+"\0"+"������application_1499049246164_2138"+"\0"+"d")));
//		System.out.println(str);
		levelDB.db.close();
	}
}
