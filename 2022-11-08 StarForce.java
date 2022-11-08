package Gui1;


import java.awt.event.*;


import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.util.Vector; // 가변 크기의 배열을 구현한 컬렉션 클래스. 메써드는 size(),get(index),get(index).length()
import java.util.*;
import javax.swing.event.*;

public class StarForce extends JFrame {
   public static int lev=0; // 강화단계 : 처음이니 0단계
   public static int sum=0; // 강화하는데 사용된 총 비용 : 처음니이 0단계
   public static int try_num=0; // 강화시도 횟수 : 처음이니 0단계
   public static int [] costs= {100,200,300,400,500}; // 각 단계별 강화비용 : 0>1단계로 강화하는 데 사용된 비용 // 1>2단계 // 2>3단계 // 3>4단계 //4>5단계
   public static int [] attack= {10,20,30,40,50};
   //이건 0단계부터 시작하여, 강화하여 증가되는 공격력을 나타내는 배열입니다. 공격력은 0>1 단계 공격력 증가량은 +10,1>2 단계 공격력 증가량은 +20,...,
   //4>5단계 공격력 증가량은 +50으로 잡을 것입니다.   
   
    // 여기서부터 JDBC와 관련된 클라스.
    Connection conn = null;
	ResultSet rs = null;
	Statement st = null;
	PreparedStatement ps = null;
	JTable table;
	//여기까지가 JDBC와 관련된 클라스.
	
   public static int status=50; // 처음 0단계 강화단계에서는 기본 공격력을 50으로 잡았습니다.
   
   
   
   public static void main(String[] args) {
      StarForce stars=new StarForce();   // 강화 시스템 창(JFrame 창)을 띄우게 하기 위해 StarForces 클래스 객체 생성.  
   }
   
   public StarForce() {  
	    
    	 //여기부터 MYSQL의 DB와 Java를 연결시켜주는 구간이에요.
    	try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // sql관련한 DriverManager를 불러와주는 메써드.
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/StarForces","root","1234");  // 
			//Connection 클래스의 객체 conn을, DriverManager을 이용하여 "jdbc:mysql://localhost:3306/starforces"와 
			//연결시켜주는 객체로 정의한다.
			System.out.println("DB 접속 성공!!!");
		} catch (ClassNotFoundException e) {
			System.out.println("DB 드라이버 로딩 실패 :" + e.toString());
		} catch (SQLException e) {
			System.out.println("DB 접속실패 : " + e.toString());
		} catch (Exception e) {
			System.out.println("Unkonwn error");
			e.printStackTrace();
		}
    	//여기까지가 MYSQL의 DB와 Java를 연결시켜주는 구간이에요.

    	
         // 여기서부터는 맨 처음으로 나타나는 gui창에 나타나는 각종 레이블을 선언해주는 단계입니다 (초반부 단계).
       
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
         setTitle("장비강화 창입니다."); 
         Container c=getContentPane();
         c.setLayout(null); // 배치관리자 삭제.
          
        
         JLabel poss = new JLabel("메소를 사용하여 "+lev+" 단계에서 "+(lev+1)+" 단계로 강화합니다."); //맨 처음엔 아직 강화가 하나도 되지 않은 상태이므로,맨 먼저 0단계에서 1단계로 강화했다는 레이블을 맨위에 띄워줘야 합니다. 그래서 초반부에 이 코드를 썼어용. 
         poss.setHorizontalAlignment(SwingConstants.CENTER);
         poss.setFont(poss.getFont().deriveFont(poss.getFont().getStyle() | Font.BOLD, 30f));
         poss.setBounds(207, 20, 706, 53);
         getContentPane().add(poss);
         
         JLabel sus_label = new JLabel("성공확률"); 
         sus_label.setHorizontalAlignment(SwingConstants.RIGHT);
         sus_label.setFont(sus_label.getFont().deriveFont(sus_label.getFont().getStyle() | Font.BOLD, 30f));
         sus_label.setBounds(715, 96, 135, 65);
         getContentPane().add(sus_label);
         
         JLabel fail_prob = new JLabel(8*lev+"%"); //각 단계에서의 실패확률을 알려주는 레이블입니다.
         fail_prob.setFont(fail_prob.getFont().deriveFont(fail_prob.getFont().getStyle() | Font.BOLD, 30f));
         fail_prob.setBounds(898, 171, 226, 53);
         getContentPane().add(fail_prob);
         
         JLabel des_prob = new JLabel(7*lev+"%"); //각 단계에서의 성공확률을 알려주는 레이블입니다.
         des_prob.setFont(des_prob.getFont().deriveFont(des_prob.getFont().getStyle() | Font.BOLD, 30f));
         des_prob.setBounds(898, 245, 226, 54);
         getContentPane().add(des_prob);
         
         JLabel sus_prob = new JLabel((100-15*lev)+"%");  //현재 강화단계에서 성공확률을 알려주는 레이블입니다.  0>1단계는 100% , 1>2단계는 85%, 2>3단계는 70%입니다. >> 즉, 강화단계가 1단계 올라갈 때마다 성공확률이 15퍼씩 떨어지므로, 이 코드를 썼어용. 
         sus_prob.setFont(sus_prob.getFont().deriveFont(sus_prob.getFont().getStyle() | Font.BOLD, 30f));
         sus_prob.setBounds(898, 96, 226, 65);
         getContentPane().add(sus_prob);
         
         JLabel cost = new JLabel(" "); // 현재 강화단계에서 한 단계 위로 강화시도하는데 사용될 강화비용을 나타내는 레이블입니다.
         cost.setText(costs[lev]+"만 메소"); // 맨 처음엔 아직 강화가 하나도 되지 않은 상태이므로, 강화 0단계>>1단계로 가는데 사용된 비용을 먼저 선언해주어야 합니다. 따라서 초반부에 이 코드를 사용했어요. 이 부분은 차후에 매번 강화시도마다 증가하는 부분입니다. 예시는 16번째줄을 참고해주세요.
         
         cost.setFont(cost.getFont().deriveFont(cost.getFont().getStyle() | Font.BOLD, 30f));
         cost.setBounds(896, 377, 187, 65);
         getContentPane().add(cost);
         
         JLabel cost_label = new JLabel(lev+"단계 >> "+(lev+1)+"단계 강화비용");
         cost_label.setHorizontalAlignment(SwingConstants.RIGHT);
         cost_label.setFont(new Font("굴림", Font.BOLD, 28));
         cost_label.setBounds(508, 378, 348, 65);
         getContentPane().add(cost_label);
         
         
         JLabel total_cost = new JLabel(sum+" 메소"); // 맨 처음엔 아직 강화가 하나도 되지 않은 상태이므로, 총 강화비용을 0으로 맞춰줍니다. 이 부분은 차후에 강화할때마다 늘어납니다. 
         total_cost.setFont(total_cost.getFont().deriveFont(total_cost.getFont().getStyle() | Font.BOLD, 30f));
         total_cost.setBounds(896, 477, 247, 59);
         getContentPane().add(total_cost);
         
         JLabel total_num = new JLabel(try_num+" 회"); //맨 처음엔 아직 강화가 하나도 되지 않은 상태이므로,강화시도를 0으로 뜨게 하는 코드입니다. 이 부분은 차후에 매번 강화시도마다 1회씩 증가하는 부분입니다.
         total_num.setFont(total_num.getFont().deriveFont(total_num.getFont().getStyle() | Font.BOLD, 30f));
         total_num.setBounds(896, 555, 187, 53);
         getContentPane().add(total_num);
         
         JLabel attackstatus = new JLabel("  현재 공격력 :    "+status); // 맨 처음엔 아직 강화가 하나도 되지 않은 상태이므로, 현재공격력을 기본 50으로 맞춰줍니다. 이 부분도 강화단계가 1단계씩 올라갈때마다 증가합니다.
         attackstatus.setHorizontalAlignment(SwingConstants.LEFT);
         attackstatus.setFont(attackstatus.getFont().deriveFont(attackstatus.getFont().getStyle() | Font.BOLD, 30f));
         attackstatus.setBounds(32, 457, 463, 53);
         getContentPane().add(attackstatus);
         
         JLabel attack_increment = new JLabel("공격력 증가량    "+attack[lev]); //맨 처음엔 아직 강화가 하나도 되지 않은 상태이므로, 0>1 단계 강화시 공격력 증가량을 나타냅니다. 단계가 올라갈수록 공격력 증가량도 높아지게 됩니다.
         attack_increment.setHorizontalAlignment(SwingConstants.LEFT);
         attack_increment.setFont(attack_increment.getFont().deriveFont(attack_increment.getFont().getStyle() | Font.BOLD, 30f));
         attack_increment.setBounds(658, 309, 450, 53);
         getContentPane().add(attack_increment);
         
         JPanel panel_of_sword_image = new JPanel();
         panel_of_sword_image.setBounds(42, 80, 463, 347);
         getContentPane().add(panel_of_sword_image);
         panel_of_sword_image.setLayout(null);
         
         JLabel lblNewLabel_2 = new JLabel("");
         lblNewLabel_2.setIcon(new ImageIcon("C:\\Users\\gram\\Desktop\\프로젝트 이미지\\왕푸222.png"));
         lblNewLabel_2.setBounds(0, 5, 400, 342);
         panel_of_sword_image.add(lblNewLabel_2);
         
    
         JLabel total_cost_lbl = new JLabel("현재까지 쓴 강화비용");
         total_cost_lbl.setHorizontalAlignment(SwingConstants.RIGHT);
         total_cost_lbl.setFont(total_cost_lbl.getFont().deriveFont(total_cost_lbl.getFont().getStyle() | Font.BOLD, 30f));
         total_cost_lbl.setBounds(529, 469, 321, 74);
         getContentPane().add(total_cost_lbl);
         
         JLabel total_num_lbl = new JLabel("현재까지 총 강화횟수");
         total_num_lbl.setHorizontalAlignment(SwingConstants.RIGHT);
         total_num_lbl.setFont(new Font("굴림", Font.BOLD, 30));
         total_num_lbl.setBounds(529, 569, 317, 39);
         getContentPane().add(total_num_lbl);
         
         JLabel fail_lbl = new JLabel("실패확률(파괴x)");
         fail_lbl.setHorizontalAlignment(SwingConstants.RIGHT);
         fail_lbl.setFont(new Font("굴림", Font.BOLD, 30));
         fail_lbl.setBounds(613, 170, 237, 54);
         getContentPane().add(fail_lbl);
         
         JLabel destroy_lbl = new JLabel("파괴확률");
         destroy_lbl.setHorizontalAlignment(SwingConstants.RIGHT);
         destroy_lbl.setFont(new Font("굴림", Font.BOLD, 30));
         destroy_lbl.setBounds(715, 243, 135, 56);
         getContentPane().add(destroy_lbl);
         
         
         class  JTableEx2 extends JFrame{
        	 public JTableEx2() {
        		 try {
          			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/StarForces","root","1234");
          			st = conn.createStatement();
             			String sql = "SELECT * FROM forcrud";
             			rs = st.executeQuery(sql);
             			String cols[] = {"일련번호", "현재강화상태", "강화성공여부","총비용"};	
             			DefaultTableModel model=new DefaultTableModel(cols, 0);
             			table=new JTable(model);
             			JScrollPane scroll = new JScrollPane(table);
             			JButton confirm = new JButton("확인완료");
             			JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
             			confirm.addActionListener(new ActionListener() {
             	             public void actionPerformed(ActionEvent e) {
             	            	 dispose();
             	             }
             	          });

             			// 받은 결과값을 출력
             			while (rs.next()) {
             				String id = rs.getString(1);
             				String statuses = rs.getString(2);
             				String susFail = rs.getString(3);
             				String Totalcos = rs.getString(4);
             				Vector<String> v= new Vector<String>();
             				v.add(id);
             				v.add(statuses);
             				v.add(susFail);
             				v.add(Totalcos);
             				model.addRow(v);
             				System.out.println(id+" , "+statuses+" , "+susFail);
             			}
             			Container c = getContentPane();
             			c.add("South", p);
             			c.add("Center",scroll);
             			p.add(confirm);
             			setBounds(400,100,755,479);
             			setVisible(true);
             			
             			rs.close();
             			st.close();
             			conn.close();
             			
             		}
        		 
        		 
          		 catch (SQLException f) {
             			f.printStackTrace();
             		}finally {
             			try {
             				if (rs != null)
             					rs.close();
             				if (st != null)
             					st.close();
             				if (ps != null)
             					ps.close();
             			} catch (Exception g) {
             				System.out.println(g + "=> dbClose fail");
             			}
          		  }
        	 }       	 
         }
         JButton HELL_MODE = new JButton("모든 강화기록");
         HELL_MODE.setFont(HELL_MODE.getFont().deriveFont(HELL_MODE.getFont().getStyle() | Font.BOLD, 30f));
         HELL_MODE.setBounds(32, 632, 212, 53);
         getContentPane().add(HELL_MODE);
         HELL_MODE.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
            	 new JTableEx2();
             }
          });
         
         
         
      //여기까지 gui창에 나타나는 각종 레이블을 선언해주는 단계였습니다.
      
      
      //최종 단계인 5단계까지 강화하여 축하해주는 창입니다.
         class Congratulate extends JFrame{
            public Congratulate() {
               
               Container c=getContentPane();
               c.setLayout(null);
               getContentPane().setBackground(Color.WHITE);
               JLabel congratulate=new JLabel("5단계 강화를 축하합니다!!!");
               congratulate.setForeground(Color.BLACK);
               congratulate.setFont(new Font("궁서", Font.BOLD, 85));
               congratulate.setHorizontalAlignment(SwingConstants.CENTER);
               congratulate.setBounds(168, 128, 1210, 214);
               
               JButton full_level_check = new JButton("취소");
               full_level_check.setBackground(Color.GREEN);
               full_level_check.setFont(new Font("궁서", Font.BOLD, 55));
               full_level_check.setBounds(792, 523, 275, 82);
               full_level_check.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        dispose(); // 5단계 강화를 축하해주는 창에서 확인을 누를시, 창을 닫아줍니다.
                     }
                  });
               
               JLabel check_talk1 = new JLabel("처음부터 다시 강화 하시겠습니까?");
               check_talk1.setForeground(Color.BLACK);
               check_talk1.setFont(new Font("궁서", Font.BOLD, 60));
               check_talk1.setHorizontalAlignment(SwingConstants.CENTER);
               check_talk1.setBounds(224, 366, 1154, 147);
               
               
               c.add(congratulate);
               c.add(full_level_check);
               c.add(check_talk1);
               
               JButton again_0_level = new JButton("확인");
               again_0_level.addActionListener(new ActionListener() { // 5단계 강화를 축하해주는 창에서 처음부터 다시 강화시뮬레이션을 해보기 위해 확인을 누를시, 모든 것(예를 들어,강화단계나 강화시도횟수 포함)이 초기화가 되도록 만들어줍니다.
                  public void actionPerformed(ActionEvent e) {
                     lev=0; // 강화단계 : 초기화시키므로 0단계
                     sum=0; 
                     try_num=0;
                     status=50;//현재 공격력 : 0단계 상태로 돌아갔기 때문에, ㄱㅣ본 공격력인 50으로 초기화 시켜줍니다. 
                     
                     //각 요소들이 초기화되어 값이 변했기 때문에, 레이블도 마찬가지로 바꿔줘야 합니다.
                     poss.setText("메소를 사용하여 "+lev+" 단계에서 "+(lev+1)+" 단계로 강화합니다.");
                     fail_prob.setText(8*lev+"%");
                     des_prob.setText(7*lev+"%");
                     sus_prob.setText((100-15*lev)+"%");
                     cost.setText(costs[lev]+"만 메소");
                     cost_label.setText(lev+"단계 >> "+(lev+1)+"단계 강화비용");
                     total_cost.setText(sum+" 메소");
                     total_num.setText(try_num+" 회");
                     attackstatus.setText("  현재 공격력 :    "+status);
                     attack_increment.setText("공격력 증가량    "+attack[lev]);
                     
                     
                     dispose(); // 5단계 강화를 축하해주는 창에서 확인을 누를시, 창을 닫아줍니다.
                     
                  }
               });
               
               
               again_0_level.setFont(new Font("궁서", Font.BOLD, 55));
               again_0_level.setBackground(Color.GREEN);
               again_0_level.setBounds(426, 523, 275, 82);
               getContentPane().add(again_0_level);
               
               setBounds(-30,-30, 2000,2000);
               setVisible(true);
         
              } 
         }   
            
      //"강화 성공"을 알려주는 창입니다.
         class Success extends JFrame{
            public Success() {
               Container c=getContentPane();
               c.setLayout(null);
               JLabel sud=new JLabel("강화 성공!!");
               sud.setFont(sud.getFont().deriveFont(sud.getFont().getStyle() | Font.BOLD, 40f));
               sud.setHorizontalAlignment(SwingConstants.CENTER);
               sud.setBounds(29, 22, 522, 112);
               JButton btnNewButton_1 = new JButton("확인");
               btnNewButton_1.setFont(btnNewButton_1.getFont().deriveFont(btnNewButton_1.getFont().getStyle() | Font.BOLD, 30f));
               btnNewButton_1.setBounds(235, 192, 100, 48);
               btnNewButton_1.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        dispose(); // 강화 시도여부를 재차 질문하는 창에서 취소를 누를시, 강화 시도여부를 재차 질문하는 창을 닫아줍니다.을 닫아줍니다.
                        if (lev==5) {
                           dispose();
                           new Congratulate();
                        }
                     }
                  });   
               c.add(btnNewButton_1);
               c.add(sud);
               
               setBounds(450,300, 600, 300);
               setVisible(true);
            }
         }
         
      //"강화 실패"를 알려주는 창입니다.
         class Fail extends JFrame{
            public Fail() {
               Container c=getContentPane();
               c.setLayout(null);
               JLabel fai=new JLabel("강화 실패..");
               fai.setFont(fai.getFont().deriveFont(fai.getFont().getStyle() | Font.BOLD, 40f));
               fai.setHorizontalAlignment(SwingConstants.CENTER);
               fai.setBounds(29, 22, 522, 112);
               JButton btnNewButton_1 = new JButton("확인");
               btnNewButton_1.setFont(btnNewButton_1.getFont().deriveFont(btnNewButton_1.getFont().getStyle() | Font.BOLD, 30f));
               btnNewButton_1.setBounds(235, 192, 100, 48);
               btnNewButton_1.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        dispose(); // 강화 시도여부를 재차 질문하는 창에서 취소를 누를시, 강화 시도여부를 재차 질문하는 창을 닫아줍니다.을 닫아줍니다.
                     }
                  });   
               c.add(btnNewButton_1);
               c.add(fai);
               
               setBounds(450,300, 600, 300);
               setVisible(true);
            }
         }
         
      //"아이템 파괴"를 알려주는 창입니다.
         
         class Destroy extends JFrame{
            public Destroy() {
	              try {
	            	  conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/StarForces","root","1234");
	            	  String query = "delete from forcrud";
                      st = conn.createStatement(); // 이거 왜 있는지 모르겠음.
                      st.executeUpdate(query);
                      System.out.println("강화를 시도한 기록이 모오오오오~~~두 지워져버렸네요 ^^.");
                      conn.close();
                  } catch (Exception exception) {
                      exception.printStackTrace();
                  }
        
                  getContentPane().setBackground(Color.BLACK);
                  Container c=getContentPane();
                  c.setLayout(null);
                  JLabel destroy=new JLabel("아이템이 파괴되었습니다.");
                  destroy.setForeground(Color.WHITE);
                  destroy.setFont(new Font("궁서", Font.BOLD, 95));
                  destroy.setHorizontalAlignment(SwingConstants.CENTER);
                  destroy.setBounds(168, 128, 1210, 214);
                  JButton btnNewButton_1 = new JButton("확인");
                  btnNewButton_1.setBackground(Color.YELLOW);
                  btnNewButton_1.setFont(new Font("궁서", Font.BOLD, 55));
                  btnNewButton_1.setBounds(623, 375, 275, 82);
                  btnNewButton_1.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                           System.exit(0); //"아이템이 파괴되었습니다. 확인버튼을 눌러서 이 창을 닫아주십시오."가 뜨는 창에서 확인버튼 클릭 시,모든 창(JFrame창)이 종료되게 하는 코드입니다.
                        }
                     });   
                  c.add(btnNewButton_1);
                  c.add(destroy);
                  
                  
                  
                  JLabel lblNewLabel = new JLabel("확인버튼을 눌러서 이 창을 닫아주십시오.");
                  lblNewLabel.setForeground(Color.WHITE);
                  lblNewLabel.setFont(new Font("궁서", Font.BOLD, 60));
                  lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
                  lblNewLabel.setBounds(224, 480, 1154, 147);
                  getContentPane().add(lblNewLabel);
                  
                  JLabel lblNewLabel_1 = new JLabel("--도박신고 번호 1336--");
                  lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
                  lblNewLabel_1.setForeground(Color.WHITE);
                  lblNewLabel_1.setFont(new Font("궁서", Font.BOLD, 60));
                  lblNewLabel_1.setBounds(224, 620, 1154, 147);
                  getContentPane().add(lblNewLabel_1);
                  
                  setBounds(-30,-30, 2000,2000);
                  setVisible(true);
               } 
         }
         
         // MySQL에 저장된 모든 강화기록을 사게할지 말지 재차 질문하는 창입니다. 
         class DeleteCheck extends JFrame{
             public DeleteCheck() {
            	 Container c=getContentPane();
                 c.setLayout(null);
                 JLabel question=new JLabel("정말로 이전 기록을 삭제하시겠습니까??");
                 question.setFont(question.getFont().deriveFont(question.getFont().getStyle() | Font.BOLD, 40f));
                 question.setHorizontalAlignment(SwingConstants.CENTER);
                 question.setBounds(57, 41, 761, 112);
                 JButton real_delete = new JButton("확인");
                 real_delete.setFont(real_delete.getFont().deriveFont(real_delete.getFont().getStyle() | Font.BOLD, 30f));
                 real_delete.setBounds(235, 188, 178, 64);
                 real_delete.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) {
                          dispose(); 
                          try {
                          	  conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/StarForces","root","1234");
                          	  String query = "delete from forcrud";
                              st = conn.createStatement(); 
                              st.executeUpdate(query);
                              JOptionPane.showMessageDialog(real_delete, "이전 아이템에 대한 강화기록이 모두 삭제되었습니다.");
                              System.out.println("이전 아이템에 대한 강화기록이 모두 삭제되었습니다.");
                              conn.close();
                          } catch (Exception exception) {
                              exception.printStackTrace();
                          }
                       
                       }
                    });   
                 c.add(real_delete);
                 c.add(question);
                 
                 JButton cancel_delete = new JButton("취소");
                 cancel_delete.setFont(cancel_delete.getFont().deriveFont(cancel_delete.getFont().getStyle() | Font.BOLD, 30f));
                 cancel_delete.setBounds(467, 188, 178, 64);
                 getContentPane().add(cancel_delete);
                 cancel_delete.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        dispose(); 
                     }
                  }); 
                 
                 setBounds(300,250, 909, 364);
                 setVisible(true);
             }
          }
         
        
         
         
      //여기서부터는 장비강화 창에서 "강화시작!"버튼을 눌렀을 때 강화 시도여부를 재차 질문하는 창 즉,"class Che"를 정의해주는 단계입니다. 
         class Che extends JFrame{ 
            public Che() {
            Container c=getContentPane();
            c.setLayout(null); // 배치관리자 삭제.
            
            JLabel lblNewLabels = new JLabel("강화 시도하시겠습니까??"); //강화 시도여부를 재차 질문하는 창에서 "강화 시도하겠어요?"가 나오게 함.
            lblNewLabels.setFont(lblNewLabels.getFont().deriveFont(lblNewLabels.getFont().getStyle() | Font.BOLD, 40f));
            lblNewLabels.setHorizontalAlignment(SwingConstants.CENTER);
            lblNewLabels.setBounds(29, 22, 522, 112);
            getContentPane().add(lblNewLabels);
            
            JButton btnNewButton = new JButton("강화");
            btnNewButton.setFont(btnNewButton.getFont().deriveFont(btnNewButton.getFont().getStyle() | Font.BOLD, 30f));
            btnNewButton.setBounds(97, 192, 153, 48);
            btnNewButton.addActionListener(new ActionListener() { //여기서부터 강화 시도여부를 재차 질문하는 창에서 "강화"버튼의 리스너를 정의하고 추가하는 코드입니다. 단 이 부분은 위쪽 초반부를 먼저 다쓴 다음에, 작성해야 합니다. 
                  public void actionPerformed(ActionEvent e) {
                     
                     JButton b=(JButton)e.getSource();
                     int rsul=(int)(Math.random()*100)+1; //1부터 100까지의 정수 중, 랜덤으로 나온 숫자 하나가 변수 rsul안에 저장이 됩니다. 그리면 각 숫자가 뽑힐 확률은 1%로 동일합니다. 예를 들어 70이하의 숫자가 나올 확률은 70%가 됩니다. 
                     
                     
                     if(rsul<=100-lev*15) { // lev>>(lev+1)단계로의 강화성공률은 (100-lev*15) 즉, rsul<=100-lev*15 가 될 확률입니다.
                        
                        String susfail="성공";
                        dispose(); // 강화 시도여부를 재차 질문하는 창을 닫아줍니다.
                        new Success();  // 강화에 성공했다는 창을 띄워주는 코드입니다.
                        try_num+=1; // 강화를 한번 시도 하였으므로, 강화한 시도횟수를 먼저 1늘려줘야합니다.
                        total_num.setText(try_num+" 회"); //강화 시도횟수가 바로 위의 코드에서 갱신이 되었으므로, 다시 레이블을 작성해주어야 합니다.
                        int trys=try_num;
                        sum+=costs[lev]; //강화 비용이 한번 발생하였으므로, 총 사용된 강화비용을 갱신해주어야 합니다.
                        int sums=sum;
                        status+=attack[lev];//강화에 성공하였으므로, 현재 공격력에 지정된 공격력 증가량을 더해줍니다. 예를 들어, 0 > 1단계 강화 성공 시, 공격력 증가량이 10이므로, 현 공격력은 50+10=60이 됩니다.
                        // 1 > 2단계 강화 성공 시, 공격력 증가량이 20이므로, 현 공격력은 50+10+20=80이 됩니다.
                        attackstatus.setText("  현재 공격력 :    "+status); //현 공격력이 갱신되었으므로, 다시 레이블을 갱신해주어야 합니다.
                        total_cost.setText(sum+"만 메소");//총 사용된 강화비용이 갱신되었으므로, 다시 레이블을 갱신해주어야 합니다.
                        lev+=1; //101번째 코드에서 보시는 것처럼 강화성공했다는 조건을 만족하면, 당연히 강화단계를 1단계 늘려줘야 합니다.
                        int levs=lev;
                        try {
                        	conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/StarForces","root","1234");
                            String query = "INSERT INTO forcrud values('" + trys + "','" + levs + "','" + susfail + "','" + sums + "')";
                            st = conn.createStatement(); // 이거 왜 있는지 모르겠음.
                            st.executeUpdate(query);
                            System.out.println("강화를 시도한 기록이 MYSQL문에 이상없이 전달되었습니다.");
                            conn.close();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        
                        
                        
                        
                        if (lev<5) { //강화단계는 최고 5단계까지 있으므로, 5>>6단계는 없습니다. 따라서 5단계 미만인 상태에서만 강화비용이 존재하므로, 이 코드를 선언하였습니다.
                           cost.setText(costs[lev]+"만 메소");
                           attack_increment.setText("공격력 증가량    "+attack[lev] );
                           cost_label.setText(lev+"단계 >> "+(lev+1)+"단계 강화비용");// 강화단계가 5단계 미만인 상태에서의 강화비용을 나타냅니다.
                            poss.setText("메소를 사용하여 "+lev+" 단계에서 "+(lev+1)+" 단계로 강화합니다."); //강화단계는 최고 5단계까지 있으므로, 5>>6단계는 없습니다. 따라서 5단계 미만인 상태에서만 lev+" 단계에서 "+(lev+1)+" 단계로 강화합니다." 라는 문구가 나오게 해야 합니다.
                            sus_prob.setText((100-15*lev)+"%"); 
                            fail_prob.setText(8*lev+"%");
                            des_prob.setText(7*lev+"%");
                            
                            }
                        
                        
                        else  { //여기는 강화성공하고, 강화성공 이후의 단계가 최고 단계인 5단계일 때를 나타내므로, 더이상 강화를 할 수 없는 상태입니다. 따라서 바로 밑의 코드가 실행되게 만들어야 합니다. 
                           
                           poss.setText("축하합니다.모든 단계를 강화하였습니다."); 
                           sus_prob.setText("---------------");
                           fail_prob.setText("--------------");
                           des_prob.setText("--------------");
                           }
                    
                     
                     
                     }
                     
                     else if(rsul<=100-lev*15+7*lev){// 각 단계에서의 파괴확률은 7*lev%입니다. 다음은 조건은 강화를 시도하였으나, 실패하여 아이템이 파괴되었다는 것을 의미합니다.. 
                        dispose();
                        
                        new Destroy();
                        
                     }
                     
                     
                     else { //각 단계에서의 실패확률은 8*lev%입니다. 강화단계가 5단계 미만인 상태이고, 강화를 실패했다는 조건을 의미합니다. 
                    	String susfail="실패";
                        dispose(); // 강화 시도여부를 재차 질문하는 창을 닫아줍니다.
                        new Fail(); 
                        try_num+=1; // 강화를 한번 시도 하였으므로, 강화한 시도횟수를 먼저 1늘려줘야합니다.
                        int trys=try_num;
                        total_num.setText(try_num+" 회"); //강화 시도횟수가 바로 위의 코드에서 갱신이 되었으므로, 다시 레이블을 작성해주어야 합니다.
                        sum+=costs[lev];//강화 비용이 한번 발생하였으므로, 총 사용된 강화비용을 갱신해주어야 합니다.
                        int sums=sum;
                        int levs=lev;
                        total_cost.setText(sum+"만 메소");//총 사용된 강화비용이 갱신되었으므로, 다시 레이블을 갱신해주어야 합니다.
                        try {
                        	conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/StarForces","root","1234");
                        	String query = "INSERT INTO forcrud values('" + trys + "','" + levs + "','" + susfail + "','" + sums + "')";
                            st = conn.createStatement(); // 이거 왜 있는지 모르겠음.
                            st.executeUpdate(query);
                            System.out.println("강화를 시도한 기록이 MYSQL문에 이상없이 전달되었습니다.");
                            conn.close();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                     }
                  }
               });
            c.add(btnNewButton);
            
            JButton btnNewButton_1 = new JButton("취소");
            btnNewButton_1.setFont(btnNewButton_1.getFont().deriveFont(btnNewButton_1.getFont().getStyle() | Font.BOLD, 30f));
            btnNewButton_1.setBounds(326, 192, 165, 48);
            btnNewButton_1.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     dispose(); // 강화 시도여부를 재차 질문하는 창에서 취소를 누를시, 강화 시도여부를 재차 질문하는 창을 닫아줍니다.을 닫아줍니다.
                  }
               });   
            c.add(btnNewButton_1);
            
            setBounds(450,300, 600, 300);
            setVisible(true);
            }
       }
       //여기까지가 장비강화 창에서 "강화시작!"버튼을 눌렀을 때 강화 시도여부를 재차 질문하는 창 즉,"class Che"를 정의해주는 단계였습니다.
         
         
         
         
         JButton start = new JButton("강화시작!");
         start.setFont(start.getFont().deriveFont(start.getFont().getStyle() | Font.BOLD, 30f));
         start.setBounds(32, 534, 463, 74);
         start.addActionListener(new ActionListener() {//강화시작 버튼을 눌렀을 때 강화상태가 5단계 미만이면 강화 시도여부를 재차 질문하는 창(Che)이 뜨게 하고, 5단계일때는 강화시작 버튼의 레이블을 "강화 끝!"으로 바꿔줍니다. .
            public void actionPerformed(ActionEvent e) {
               if(lev!=5)new Che();  
               else start.setText("강화 끝!");  
            }
         });
         
         getContentPane().add(start);
         
         
         JButton Delete = new JButton("강화기록삭제");
         Delete.setFont(Delete.getFont().deriveFont(Delete.getFont().getStyle() | Font.BOLD, 30f));
         Delete.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
            	 new DeleteCheck();
             }
          });
         Delete.setBounds(267, 632, 226, 53);
         getContentPane().add(Delete);
         
         
         setBounds(180, 10, 1150, 800);
         setResizable(false);
         setVisible(true);
      }
}

