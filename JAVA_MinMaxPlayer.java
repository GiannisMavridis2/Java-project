package fidaki;
import java.util.ArrayList;
/* 
 * � ����� ���� �������������� ���� ������ ��� �������� �� ���� ���
 * �� ���� ��� ��������� MinMax 
 * ���������� ��� Player ��� �������� ��� ����� ���������� ��� ����������� �� ��� HeuristicPlayer 
 * E����� �������� ��� ����������� ��� ��������� ��� MinMax Algortihm (createMySubtree,createOpponentSubtree
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
		double f;//� ���� ��� ����������-�����������
		int[] kinhsh=new int[5];
		int steps,gainPoints,skorPrin;
		tempPoints=0;
		skorPrin=getScore(); //������� �� ���� ��� ������ ���� ��� ��������� ��� ������� ����� 
		kinhsh=move(currentPos,dice,true,false,false);//������� �� move �� ������ ��� ������ ��������� a true 
		//��� �� ��������� ��� ���� ������� ���� ���������� ��� ������� ��� ��� ���������� ������
		//���� � move ��� �� ���������� �� ������ �� ��� �� ������ ������
		steps=kinhsh[0]-currentPos;//���� ������ ����� ���� ������ ����
		gainPoints=tempPoints-skorPrin;//������ ������� �������
		f=steps*0.65+gainPoints*0.35;
		return f;
	}
	
	public int getNextMove(int currentPos,Board board,int opponentCurrentPos){
		Node root=new Node(null,null,0,board,0);//� ���� ��� ������� ���
		root.setChildren(new ArrayList<Node>(6));
		createMySubtree(root,0,currentPos,opponentCurrentPos);//��������� �� ������ ���
		setBoard(board);
		int thisDice=chooseMinMaxMove(root);//������� ��� ����� ���
		int skorPrin;
		int[] kinisi=new int[5];
		int[] kinisiPath=new int[6];
		skorPrin=getScore();
		kinisi=move(currentPos,thisDice,false,false,true);//���� ������� ��� ���������� �������� ������ ��� ��������� �� ������
		//���� refresh �� path
		kinisiPath[0]=thisDice; 
		kinisiPath[1]=getScore()-skorPrin; 
		kinisiPath[2]=kinisi[0]-currentPos; 
		kinisiPath[3]=kinisi[3]+kinisi[4];
		kinisiPath[4]=kinisi[1];
		kinisiPath[5]=kinisi[2];
		path.add(kinisiPath);
		return kinisi[0];
	}
	
	//� ��������� ���� ���������� �� ������ 
	void createMySubtree(Node parent, int depth, int currentPos, int opponentCurrentPos){
		int i;
		Board tempBoard;
		double pointsEvaluation;
		for(i=0;i<6;i++){//��� ���� ��� ��� ��� 6 ������� ������ ��� -�������� ���
			tempBoard=parent.getNodeBoard();
			pointsEvaluation=evaluateMyMove(currentPos,i+1);//���������� ��� ������ ����
			setBoard(new Board(tempBoard));
			move(currentPos,i+1,false,true,false);//������ ��� ��� ��������� ��� ������
			Node child=new Node(parent,null,1,getBoard(),0);//������ ���� ��� �����-����� ��� 
		    //���������� ���� ��� ��� ���������
			child.setChildren(new ArrayList<Node>(6));
			parent.getChildren().add(child);//�������� ��� ��� ������
			createOpponentSubtree(child,1,opponentCurrentPos,pointsEvaluation);//��� ����� i �����
			//� ��������� ������ �� ������ ��� ��� ��� 6 ������� ������ ���. ������ �� ���������
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
			//������� ������� �� ������ ������� evaluate ���� ������� ��� 1�� ��������
			//����������� ��� ����� ��� ������� ��� ��� �� min �� ������� �� ���� ��� ������
			min=root.getChildren().get(i).getChildren().get(0).getNodeEvaluation();
			for(j=1;j<6;j++){
				if (root.getChildren().get(i).getChildren().get(j).getNodeEvaluation()<min){
					min=root.getChildren().get(i).getChildren().get(j).getNodeEvaluation();
				}
			}
			root.getChildren().get(i).setNodeEvaluation(min);
			
		}
		pos=0;
		//��������� ���� ��� �� 6 ������ ��� ����� ���� max ���� 
		//���� �� ����� ��� � ���� ��� �����
		max=root.getChildren().get(0).getNodeEvaluation();
		for(i=1;i<6;i++){
			if(root.getChildren().get(i).getNodeEvaluation()>max){
			   max=	root.getChildren().get(i).getNodeEvaluation();
			   pos=i;//�� �� i-���� ����� ���� max ���� ���� � ����� ��� �� ������� �� ����� i+1
			}
		}
		root.setNodeEvaluation(max);
		return pos+1;
	}
	
	public void statistics(){
	    int[] temp=new int[6];
		temp=path.get(foresStatistics);
		int rounds=foresStatistics+1;
		System.out.println("o ������� ��� ����:"+rounds+" ����� �� ���� ��� �� "+temp[0]);
		System.out.println("������� "+ temp[5]+" ������");
		ladders=ladders+temp[5];
		System.out.println("���������� ��� "+ temp[4]+" �����");
		snakes=snakes+temp[4];
		System.out.println("����� "+ temp[3]+" ����");
		apples=apples+temp[3];
		System.out.println("���������� ��� �� ������ ��� �������� ��� ������:");
	    System.out.println("������: "+ladders);
	    System.out.println("�����: " + snakes);
	    System.out.println("����: "+ apples);
		foresStatistics++;//��������� �� ��������� ���� 1 ���� ��� ������� ���� ��� �� ������ � ��������� �� ���� �� �������� ��� �������� �����
     }	
}
