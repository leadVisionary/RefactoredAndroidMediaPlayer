package android.media.player.external;

public abstract class Handler {
	
	public Handler(Looper looper){
		
	}
	
	public void handleMessage(Message msg){
		
	}
	public Message obtainMessage(int what, int arg1, int arg2, Object obj){
		return null; // dummy, should be adapted
	}
	
	public void removeCallbacksAndMessages(Object token){
		
	}
	
	public boolean sendMessage(Message msg){
		return false;//dummy, should be adapted
	}
}
