package fidaki;
import java.util.ArrayList;
/* 
 * η κλάση αυτή αντιπροσωπεύει έναν παίκτη που επιλέγει το ζάρι του
 * με βάση τον αλγόριθμο MinMax 
 * κληρονομεί την Player και περιέχει τις ίδιες μεταβλητές και συναρτήσεις με την HeuristicPlayer 
 * Eπίσης περιέχει τις συναρτήσεις που υλοποιούν τον MinMax Algortihm (createMySubtree,createOpponentSubtree
 * ,chooseMinMaxMove)
 */

public class MinMaxPlayer extends Player {
	private ArrayList<int[]> path;
	private int foresStatistics=0,snakes=0,ladders=0,apples=0;

	public MinMaxPlayer(int id,int skor,String onoma,Board b){
		super(id,skor,onoma,b);
		path=new ArrayList<int[]>();
	}
	
	public double evaluateMyMove(int currentPos,int dice){
		double f;//η τιμή της συνάρτησης-αξιολόγησης
		int[] kinhsh=new int[5];
		int steps,gainPoints,skorPrin;
		tempPoints=0;
		skorPrin=getScore(); //κρατάμε το σκορ του παίκτη πριν την αξιλογηση της κίνησης αυτης 
		kinhsh=move(currentPos,dice,true,false,false);//καλούμε τη move με όρισμα στη λογική μεταβλητή a true 
		//για να δηλώσουμε οτι τώρα κάνουμε απλά αξιολόγηση της κίνησης και οχι πραγματικη κινηση
		//έτσι η move δεν θα μεταβάλλει το ταμπλό πχ δεν θα σπάσει σκάλες
		steps=kinhsh[0]-currentPos;//πόσα βήματα έκανα στην κίνηση αυτή
		gainPoints=tempPoints-skorPrin;//πόσους πόντους κέρδισα
		f=steps*0.65+gainPoints*0.35;
		return f;
	}
	
	public int getNextMove(int currentPos,Board board,int opponentCurrentPos){
		Node root=new Node(null,null,0,board,0);//η ρίζα του δένδρου μου
		root.setChildren(new ArrayList<Node>(6));
		createMySubtree(root,0,currentPos,opponentCurrentPos);//δημιουργώ το δένδρο μου
		setBoard(board);
		int thisDice=chooseMinMaxMove(root);//επιλέγω την ζαριά μου
		int skorPrin;
		int[] kinisi=new int[5];
		int[] kinisiPath=new int[6];
		skorPrin=getScore();
		kinisi=move(currentPos,thisDice,false,false,true);//τώρα κάνουμε την πραγματική βελτιστη κίνηση και αλλάζουμε το ταμπλό
		//κάνω refresh το path
		kinisiPath[0]=thisDice; 
		kinisiPath[1]=getScore()-skorPrin; 
		kinisiPath[2]=kinisi[0]-currentPos; 
		kinisiPath[3]=kinisi[3]+kinisi[4];
		kinisiPath[4]=kinisi[1];
		kinisiPath[5]=kinisi[2];
		path.add(kinisiPath);
		return kinisi[0];
	}
	
	//η συνάρτηση αυτή δημιουργεί το δένδρο 
	void createMySubtree(Node parent, int depth, int currentPos, int opponentCurrentPos){
		int i;
		Board tempBoard;
		double pointsEvaluation;
		for(i=0;i<6;i++){//για κάθε μια από τις 6 δυνατές ζαριές μου -κινήσεις μου
			tempBoard=parent.getNodeBoard();
			pointsEvaluation=evaluateMyMove(currentPos,i+1);//αξιολόγησε την κίνηση αυτή
			setBoard(new Board(tempBoard));
			move(currentPos,i+1,false,true,false);//φτιάξε την νέα κατάσταση του ταμπλό
			Node child=new Node(parent,null,1,getBoard(),0);//φτιάξε έναν νέο κόμβο-παιδί που 
		    //περιγράφει αυτή την νέα κατάσταση
			child.setChildren(new ArrayList<Node>(6));
			parent.getChildren().add(child);//πρόσθεσέ τον στο δένδρο
			createOpponentSubtree(child,1,opponentCurrentPos,pointsEvaluation);//εγώ παίζω i ζαριά
			//ο αντίπαλος μπορεί να παίξει μια απο τις 6 δυνατές ζαριές του. Φτιάξε το υποδένδρο
		}
		
		
	}
	
	void createOpponentSubtree(Node parent, int depth, int opponentCurrentPos, double parentEval){
		int i;
		Board tempBoard;
		double pointsEvaluation;
		for(i=0;i<6;i++){
			tempBoard=parent.getNodeBoard();
			setBoard(new Board(tempBoard));
			pointsEvaluation=evaluateMyMove(opponentCurrentPos,i+1);
			move(opponentCurrentPos,i+1,false,true,false);
			Node child=new Node(parent,null,2,getBoard(),parentEval-pointsEvaluation);
			parent.getChildren().add(child);
		}	
	}
	
	int chooseMinMaxMove(Node root){
		int i,j,pos;
		double min,max;
		for (i=0;i<6;i++){
			//έχοντας φτιάξει το δένδρο κάνουμε evaluate τους κόμβους του 1ου επιπέδου
			//συγκρίνουμε τις τιμές των παιδιών του και τη min τη θέτουμε ως τιμή του κόμβου
			min=root.getChildren().get(i).getChildren().get(0).getNodeEvaluation();
			for(j=1;j<6;j++){
				if (root.getChildren().get(i).getChildren().get(j).getNodeEvaluation()<min){
					min=root.getChildren().get(i).getChildren().get(j).getNodeEvaluation();
				}
			}
			root.getChildren().get(i).setNodeEvaluation(min);
			
		}
		pos=0;
		//βρίσκουμε ποιο απο τα 6 παιδια της ρίζας έχει max τιμή 
		//αυτή θα είναι και η τιμή της ρίζας
		max=root.getChildren().get(0).getNodeEvaluation();
		for(i=1;i<6;i++){
			if(root.getChildren().get(i).getNodeEvaluation()>max){
			   max=	root.getChildren().get(i).getNodeEvaluation();
			   pos=i;//αν το i-οστο παιδί έχει max τιμή τότε η ζαριά που θα επιλέξω θα είναι i+1
			}
		}
		root.setNodeEvaluation(max);
		return pos+1;
	}
	
	public void statistics(){
	    int[] temp=new int[6];
		temp=path.get(foresStatistics);
		int rounds=foresStatistics+1;
		System.out.println("o παίκτης στο γύρο:"+rounds+" έθεσε το ζάρι ίσο με "+temp[0]);
		System.out.println("ανέβηκε "+ temp[5]+" σκάλες");
		ladders=ladders+temp[5];
		System.out.println("τσιμπήθηκε από "+ temp[4]+" φίδια");
		snakes=snakes+temp[4];
		System.out.println("έφαγε "+ temp[3]+" μήλα");
		apples=apples+temp[3];
		System.out.println("στατιστικά για το σύνολο των κινήσεων του παίκτη:");
	    System.out.println("σκάλες: "+ladders);
	    System.out.println("φίδια: " + snakes);
	    System.out.println("μήλα: "+ apples);
		foresStatistics++;//αυξάνουμε τη μεταβλητή κατά 1 ώστε την επόμενη φορά που θα κληθεί η συνάρτηση να πάρω τα στοιχεία του επόμενου γύρου
     }	
}
