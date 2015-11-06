import java.util.concurrent.ConcurrentLinkedQueue;

public class Guide {

		private ConcurrentLinkedQueue <String> operations;
		
		public Guide() {
			super();
			this.operations = new ConcurrentLinkedQueue();
		}

		public ConcurrentLinkedQueue<String> getOperations() {
			return operations;
		}

		public void setOperations(ConcurrentLinkedQueue<String> operations) {
			this.operations = operations;
		}


		
}
