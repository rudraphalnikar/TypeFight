import javax.swing.*; //imports
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;
import java.util.Scanner;
import java.io.*;
import javax.imageio.ImageIO;

public class GameFrame //main method class
{
	private JMenuBar b;
	private JMenu options;
	private JMenuItem pause, play, reset;
	
	public static void main(String [] args)
	{
		GameFrame gf = new GameFrame();
		gf.run();
	}
	
	public void run()//runs PanelHolder class
	{
		JFrame f = new JFrame();
		
		PanelHolder p = new PanelHolder();
		
		b = new JMenuBar();
		
		pause = new JMenuItem("Pause");
		play = new JMenuItem("Play");
		reset = new JMenuItem("Reset Progress");
		
		options = new JMenu("Options");
		
		pause.addActionListener(p);
		play.addActionListener(p);
		reset.addActionListener(p);
		
		options.add(pause);//adds items to menu
		options.add(play);
		options.add(reset);
		
		b.add(options);
		
		
		f.setTitle("Type Fight");
		f.setDefaultCloseOperation(f.EXIT_ON_CLOSE); // sets program to terminate when window is closed
		
		f.add(p); // add PanelHolder to frame	
		
		f.setJMenuBar(b); // add JMenuBar to frame
		
		f.pack();//sets the JFrame size to PanelHolder's
		f.setLocationRelativeTo(null); // centers window on screen
		f.setVisible(true);
	}
    class PanelHolder extends JPanel implements ActionListener, KeyListener
	{
		JMenuBar j;
		
		TitleScreen ts; //panels
		Instructions i;
		GamePanelHolder gph;
		EndPanel ep;

		GamePanel gp;
		TopPanelHolder tph;

		ResultsPanel rp;
		InfoPanel ip;

		Timer t;

		//TitleScreen components
		private JButton help, start;
		private JCheckBox rookie;

		//Instructions components
		private JButton home;
		private	Image blueBg, keyboard;

		//GamePanel components
		private int numWrong, numRight, charNum, lineNum, lessonNum, numLevels;
		private Font font1, font2, font3, font4;
		private double timeElapsed, acc, wpm;
		private boolean correct, levelFinished, canType;

		//ResultsPanel components
		private JButton next, retry, skip, end;
		
		//EndPanel components
		private JButton submit, finish;
		private JTextArea feedback;
		private JSlider slider;
		private JLabel status;

		private Scanner in, in2; // read from file

		private String [][] line; // lines to be typed from text file
		private String [] info;

		public PanelHolder()
		{
			setPreferredSize(new Dimension(800, 600)); // size of panel
			setLayout(new CardLayout()); 

			blueBg = getImage("instructions.jpg");//background images
			keyboard = getImage("keyboard.png");
			
			ts = new TitleScreen();//instantiates constructor
			i = new Instructions();
			gph = new GamePanelHolder();
			ep = new EndPanel();
			
			help.addActionListener(this);//add action listeners to components
			start.addActionListener(this);

			home.addActionListener(this);

			retry.addActionListener(this);
			next.addActionListener(this);
			skip.addActionListener(this);
			end.addActionListener(this);
			
			finish.addActionListener(this);
			submit.addActionListener(this);
			
			rookie.addActionListener(this);

			t = new Timer(100, this);

			add(ts, "TS"); // add panels to cardlayout, adding title screen first to make it show up at start
			add(i, "I");
			add(gph, "GPH");
			add(ep, "E");
			
			addKeyListener(this);//add key listener to panel
			
			setFocusable(true);//focus on panel
		}
		
		public void save()
		{
			File file = new File("save.txt"); // create the file to write to
			PrintWriter outFile;
			 
			try
			{
				outFile = new PrintWriter(file);
				outFile.print(lessonNum);
				outFile.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
		public Image getImage(String s) //gets the image to be drawn later, AG
		{
			File imageFile = new File(s);
			
			try
			{
				return ImageIO.read(imageFile);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
			return null;
		}

		public void actionPerformed(ActionEvent e)
		{

			if(e.getSource() == t) // to count time, increment time elapsed by 0.1 seconds every tenth of a second, AG
			{
				timeElapsed += 0.1;

				wpm = (numRight+numWrong)/6/timeElapsed*60; // assuming average english word length including spaces is 6, AG
				gp.repaint();
			}
			else if(e.getSource() == help) // move to instruction panel, RP
			{
				CardLayout cl = (CardLayout)(getLayout());
				cl.show(this, "I");
			}
			else if(e.getSource() == home) // move to title screen panel, RP
			{
				CardLayout cl = (CardLayout)(getLayout());
				cl.show(this, "TS");
				
			}
			else if(e.getSource() == start) // move to game panel when user presses start, RP
			{
				CardLayout cl = (CardLayout)(getLayout());
				cl.show(this, "GPH");

				t.start(); //  start timer when user enters the game panel, AG
				canType = true;
				
				if(rookie.isSelected())//if player knows how to play, they can skip the learning section, RP
				{
					lessonNum = 25;
				}
			}
			else if(e.getSource() == next && canType == false)//when next button is pressed, reset and go to next level, AG
			{
				CardLayout cl = (CardLayout)(tph.getLayout());
				cl.show(tph, "IP");

				requestFocusInWindow();
				canType = true;
				levelFinished = false;
				
				lessonNum++;
				save();

				lineNum = 0;
				charNum = 0;

				wpm = 0;
				acc = 0;
				numWrong = 0;
				numRight = 0;

				timeElapsed = 0;
				
				ip.repaint();
				t.restart();
			}
			else if(e.getSource() == retry)//allows user to retry level after completing it at least once, AG
			{
				CardLayout cl = (CardLayout)(tph.getLayout());
				cl.show(tph, "IP");

				requestFocusInWindow();
				
				canType = true;
				levelFinished = false;
				
				lineNum = 0;
				charNum = 0;

				wpm = 0;
				acc = 0;
				numWrong = 0;
				numRight = 0;

				timeElapsed = 0;
				ip.repaint();
				t.restart();
			}
			else if(e.getSource()== skip)//allows user to skip 5 levels under a certain circumstance, RP
			{
				CardLayout cl = (CardLayout)(tph.getLayout());
				cl.show(tph, "IP");

				requestFocusInWindow();
				
				levelFinished = false;
				canType = true;
				
				lineNum = 0;
				charNum = 0;

				wpm = 0;
				acc = 0;
				numWrong = 0;
				numRight = 0;

				timeElapsed = 0;
				
				t.restart();
				
				lessonNum+=6;
				save();
			}
			else if(e.getSource() == pause)//pause feature, AG
			{
				t.stop();
				canType = false;
				requestFocusInWindow();
			}
			else if(e.getSource() == play)//play after paused feature, AG
			{
				t.restart();
				canType = true;
				requestFocusInWindow();
			}
			else if(e.getSource() == end) // move to end panel when user is done with final level, RP
			{
				CardLayout cl = (CardLayout)(getLayout());
				cl.show(this, "E");
	        }
			else if(e.getSource() == finish) // exits the game, RP
			{
				canType = false;
				lessonNum = 0;
				save();
				System.exit(1);
	        }
			else if (e.getSource() == reset)
			{
				CardLayout cl = (CardLayout)(getLayout());
				cl.show(this, "TS");
				
				requestFocusInWindow(); // switch focus from JButton back to PanelHolder
				
				canType = false;
				
				lessonNum = 0;
			
				save();
				
				lineNum = 0;
				charNum = 0;

				wpm = 0;
				acc = 0;
				numWrong = 0;
				numRight = 0;

				timeElapsed = 0;
				
				ip.repaint();
				gp.repaint();
				
				
				t.stop();
			}
		}

		public void keyTyped(KeyEvent e)
		{
			if(canType)
			{
				if(!levelFinished)
				{

					if(e.getKeyChar() == line[lessonNum][lineNum].charAt(charNum)) // if user types correct character, AG
					{		
						if(correct)
						{
							numRight++; // increment the amount correct
						}

						correct = true; 

						charNum++; // move to next letter
					}
					else
					{
						if(correct) // so only the first time a certain letter is typed incorrectly is it counted (if user doesn't realize they made a mistake & keep typing, accuracy doesnt drop to 0), AG
						{
							numWrong++;
						}
						correct = false;
					}

				if(charNum == line[lessonNum][lineNum].length()) // if the line is finished, AG
				{
					charNum = 0; // reset charNum
					lineNum++; // increase lineNum 
				}

				if(line[lessonNum][lineNum] == null) // if the last line of the lesson is finished, AG
				{

					canType = false;
					levelFinished = true;

					CardLayout cl = (CardLayout)(tph.getLayout());
					cl.show(tph, "RP");
					
					if((acc>=90&&wpm>=15) && (lessonNum == 1 || lessonNum == 7 || lessonNum == 13 || lessonNum == 19))//Checks to see if a skip is possible, adds or doesn't add a skip button accordingly, RP
					{
						skip.setVisible(true);
					}
					else
					{
						skip.setVisible(false);
					}
					
					t.stop();
				}
				if(lessonNum == 38)//switches out buttons once user has hit the final panel, RP
				{
					end.setVisible(true);
					next.setVisible(false);
				}
				
			}
		}

			
			if(e.getKeyChar() == ']')
			{
				lessonNum++;
				ip.repaint();
			}
			

			acc = (float)numRight/(numRight+numWrong)*100; // divide the number correct by the total typed to get the accuracy, RP

			gp.repaint();
		}
		public void keyPressed(KeyEvent e){}//unneeded methods
		public void keyReleased(KeyEvent e){}
		
		class TitleScreen extends JPanel//holds two buttons as well a check box that allows user to say they know how to type
		{	
			public TitleScreen()
			{
				setBackground(Color.RED);

				setLayout(new GridLayout(3, 1));//grid layout
				
				// segmenting screen to allow for better component placement
				TopP top = new TopP();//Holds title, AG
				MiddleP middle = new MiddleP();//holds check box, RP
				JPanel bottom = new JPanel();//holds buttons, AG

				middle.setBackground(Color.RED);
				bottom.setBackground(Color.RED);

				help = new JButton("Help");
				start = new JButton("Start");

				help.setPreferredSize(new Dimension(150, 150)); // set button size
				start.setPreferredSize(new Dimension(150, 150));

				bottom.add(help);
				bottom.add(start);

				add(top);//adds panels to bigger panel
				add(middle);
				add(bottom);
			}

			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
			}

			class TopP extends JPanel // title screen text
			{
				public TopP()
				{
					setBackground(Color.RED);
				}

				public void paintComponent(Graphics g)
				{
					super.paintComponent(g);

					Font f = new Font("Serif", Font.BOLD, 40);
					g.setColor(Color.BLACK);	
					g.setFont(f);
					g.drawString("Type Fight", 310, 50);//title, AG
				}
			}
			class MiddleP extends JPanel // title screen text
			{
				public MiddleP()
				{
					setBackground(Color.RED);
					rookie = new JCheckBox("Check me if you already know how to type!");//allows user to check if they know how to type, skipping 25 levels, RP
					add(rookie);
				}

				public void paintComponent(Graphics g)
				{
					super.paintComponent(g);
				}
			}
		}

		class Instructions extends JPanel//displays the instructions for the game
		{
			public Instructions()
			{
				setLayout(null);
				setBackground(Color.CYAN);

				home = new JButton("Back to Home Screen");
				home.setBounds(600, 550, 200, 50); // set button position size, AG
				add(home);
			}
			
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);

				g.drawImage(blueBg, 0, 0, this);//background image
				Font title = new Font("Serif", Font.BOLD, 40);
				g.setFont(title);
				g.setColor(Color.BLACK);

				g.drawString("Instructions", 280, 50);

				Font instructions = new Font("Serif", Font.PLAIN, 20);
				g.setFont(instructions);

				g.drawString("Learn to type in different section that correspond to each key(s).", 0, 150);//displays instructions, RP
				g.drawString("As you type, your wpm(Words Per Minute) typed and accuracy will be displayed.",0,200);
				g.drawString("You can skip sections of keys that you are comfortable with by taking a challenge that proves", 0, 250);
				g.drawString("you know the concept.", 0, 270);
				g.drawString("You can redo any level as many times as you want to.", 0, 320);
				g.drawString("The game ends when you want to stop playing or when you finish all the possible levels.", 0, 370);

				Font message = new Font("Serif", Font.BOLD, 30);
				g.setFont(message);

				g.drawString("GOOD LUCK TYPER!", 230,550);
			}
		}

		class GamePanelHolder extends JPanel//holds GamePanel panels, AG
		{
			public GamePanelHolder()
			{
				setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

				tph = new TopPanelHolder();
				gp = new GamePanel();

				add(tph);
				add(gp);
			}
		}

		class GamePanel extends JPanel
		{
			public GamePanel()
			{
				setPreferredSize(new Dimension(800, 250));
				setLayout(new FlowLayout());

				wpm = 0;//initialize variables
				acc = 0;
				numWrong = 0;
				numRight = 0;
				timeElapsed = 0;
				charNum = 0;
				lineNum = 0;
				lessonNum = 0;
				
				getLevel();

				info = new String[100];//stores information for levels
				line = new String[100][100];//stores levels

				read(); // copy file to array

				font1 = new Font("Serif", Font.PLAIN, 25);
				font2 = new Font("Serif", Font.BOLD, 40);
			}
			
			public void getLevel() // read save.txt file for current level, AG
			{
				
				try
				{
					File file = new File("save.txt");;
					
					in2 = new Scanner(file);
									
					while (in2.hasNextLine())
					{
						lessonNum = in2.nextInt();
					}
				}
				catch (Exception e) // if there is no save file, set lessonNum to 0
				{
					lessonNum = 0;
				}
			}

			public void read()//reads text file, AG
			{
				int x = 0;
				String temp;
				numLevels = 0;

				File inFile = new File("text.txt");

				try
				{
					in = new Scanner(inFile);
				}
				catch(FileNotFoundException e)
				{
					e.printStackTrace();
					System.exit(1);
				}

				while(in.hasNext()) // while there is another line in the file, copy it to the next space in the array
				{
					temp = in.nextLine();

					if(temp.equals(""))
					{
						numLevels++;
						x = 0;
					}
					else if(temp.substring(0, 1).equals("#"))//checks to see if there is a comment(# indicates a comment)
					{
						info[numLevels] = temp.substring(1);
					}
					else
					{
						line[numLevels][x] = temp;
						x++;
					}
				}
			}
			
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);

				g.setColor(Color.BLACK);

				g.fillRect(0, 0, 800, 250); // background for bottom half of the screen

				g.setColor(Color.CYAN);

				g.fillRoundRect(210, 10, 580, 230, 40, 40); // boxes for text and diplaying wpm, accuracy, & time
				g.fillRoundRect(10, 10, 190, 70, 40, 40);
				g.fillRoundRect(10, 90, 190, 70, 40, 40);
				g.fillRoundRect(10, 170, 190, 70, 40, 40);

				g.setColor(Color.GREEN);
				g.setFont(font2);

				g.drawString("WPM:" + (int)wpm, 10, 60); // paint wpm
				g.drawString("Acc:" + (int)acc + "%", 10, 140); // paint accuracy


				if((int)timeElapsed%60 < 10) // if seconds is one digit, print a zero before Ex: 2:04, AG
				{
					g.drawString((int)timeElapsed/60+ ":0" + (int)timeElapsed%60, 20, 220);
				}
				else // else paint normally Ex: 2:42
				{
					g.drawString((int)timeElapsed/60+ ":" + (int)timeElapsed%60, 20, 220);
				}

				g.setFont(font1);
				g.setColor(Color.BLACK);

				for(int i = 0; i < 7; i++) // print out multiple lines to the screen with one statement
				{
					if(line[lessonNum][lineNum+i] != null)							// if the line doesn't exist, dont paint it, AG
						g.drawString(line[lessonNum][lineNum+i], 220, i*30+40);
				}

				if(correct) // if typed correctly, paint in green, RP
				{
					g.setColor(Color.GREEN);
				}
				else // if incorrect, paint in red, RP
				{
					g.setColor(Color.RED);
				}

				if(line[lessonNum][lineNum] != null) // print out the highlight to show what the user has typed, AG
					g.drawString(line[lessonNum][lineNum].substring(0 ,charNum), 220, 40);
			}	
		}

		class TopPanelHolder extends JPanel//holds the info panel and results panel
		{
			public TopPanelHolder()
			{
				setPreferredSize(new Dimension(800, 350));
				setLayout(new CardLayout());//sets layout to card layout

				font4 = new Font("Serif", Font.PLAIN, 25);
				
				ip = new  InfoPanel();
				rp = new  ResultsPanel();

				add(ip, "IP");
				add(rp, "RP");
			}

			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
			}
		}

		class InfoPanel extends JPanel//prints out tips on how to play the certain level
		{
			public InfoPanel()
			{
				setBackground(Color.BLUE);
			}
		
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				
				g.setColor(Color.BLACK);
				g.setFont(font4);
				
				g.drawImage(blueBg, 0, 0, this);//draws the images
				g.drawImage(keyboard, 125, 100, this);
				
				if(info[lessonNum] != null)//displays the info, AG
				{
					g.drawString(info[lessonNum], 25, 50);
				}
				
				g.setColor(Color.RED);
				
				//Highlights the keys that need to be typed, AG
				if(lessonNum == 2)//f and j
				{
					g.drawRect(125+185, 100+109, 28, 27);
					g.drawRect(125+297, 100+109, 28, 27);
					g.drawRect(125+167, 100+182, 177, 32);
				}
				else if(lessonNum == 3)//d and k
				{
					g.drawRect(125+148, 100+109, 28, 27);
					g.drawRect(125+334, 100+109, 28, 27);
				}
				else if(lessonNum == 4)//s and l
				{
					g.drawRect(125+111, 100+109, 28, 27);
					g.drawRect(125+372, 100+109, 28, 27);
				}
				else if(lessonNum == 5)//a and ;
				{
					g.drawRect(125+73, 100+109, 28, 27);
					g.drawRect(125+409, 100+109, 28, 27);
				}
				else if(lessonNum == 6)//g and h
				{
					g.drawRect(125+222, 100+109, 28, 27);
					g.drawRect(125+260, 100+109, 28, 27);
				}
				else if(lessonNum == 8)//y and t
				{
					g.drawRect(125+213, 100+72, 28, 27);
					g.drawRect(125+250, 100+72, 28, 27);
				}
				else if(lessonNum == 9)//u and r
				{
					g.drawRect(125+176, 100+109, 28, 27);
					g.drawRect(125+288, 100+109, 28, 27);
				}
				else if(lessonNum == 10)//i and e
				{
					g.drawRect(125+138, 100+109, 28, 27);
					g.drawRect(125+325, 100+109, 28, 27);
				}
				else if(lessonNum == 11)//o and w
				{
					g.drawRect(125+101, 100+109, 28, 27);
					g.drawRect(125+362, 100+109, 28, 27);
				}
				else if(lessonNum == 12)//p and q
				{
					g.drawRect(125+64, 100+72, 28, 27);
					g.drawRect(125+400, 100+72, 28, 27);
				}
				else if(lessonNum == 14)//v and b
				{
					g.drawRect(125+204, 100+109, 28, 27);
					g.drawRect(125+241, 100+109, 28, 27);
				}
				else if(lessonNum == 15)//c and n
				{
					g.drawRect(125+166, 100+109, 28, 27);
					g.drawRect(125+278, 100+109, 28, 27);
				}
				else if(lessonNum == 16)//x and m
				{
					g.drawRect(125+129, 100+109, 28, 27);
					g.drawRect(125+316, 100+109, 28, 27);
				}
				else if(lessonNum == 17)//z and ,
				{
					g.drawRect(125+92, 100+109, 28, 27);
					g.drawRect(125+353, 100+109, 28, 27);
				}
				else if(lessonNum == 18)//. and !
				{
					g.drawRect(125+7, 100+109, 28, 27);
					g.drawRect(125+390, 100+109, 28, 27);
				}
				else if(lessonNum == 20) //1 and 2
				{
					g.drawRect(125+45, 100+36, 28, 27);
					g.drawRect(125+83, 100+36, 28, 27);
				}
				else if(lessonNum == 21)//3 and 4
				{
					g.drawRect(125+120, 100+36, 28, 27);
					g.drawRect(125+157, 100+36, 28, 27);
				}
				else if(lessonNum == 22)//5 and 6
				{
					g.drawRect(125+194, 100+109, 28, 27);
					g.drawRect(125+232, 100+109, 28, 27);
				}
				else if(lessonNum == 23)//7 and 8
				{
					g.drawRect(125+269, 100+109, 28, 27);
					g.drawRect(125+307, 100+109, 28, 27);
				}
				else if(lessonNum == 24)//9 and 0
				{
					g.drawRect(125+344, 100+36, 28, 27);
					g.drawRect(125+381, 100+36, 28, 27);
				}
			}
		}

		class ResultsPanel extends JPanel//displays options of what the user can do after level is completed
		{
			public ResultsPanel()
			{
				setBackground(Color.GREEN);

				next = new JButton("Next Level");//goes to the next level, AG
				retry = new JButton("Retry Level");//replays the level, AG
				skip = new JButton("Skip 5 Levels");//skips 5 levels if a condition is met, RP
				end = new JButton("Finish");//goes to the final panel when no more levels are remaining, RP

				add(retry);
				add(next);
				add(skip);
				skip.setVisible(false);
				add(end);
				end.setVisible(false);
				
				font3 = new Font("Serif", Font.BOLD, 45);
			}

			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				g.drawImage(blueBg, 0, 0, this);
				g.setFont(font3);
				g.setColor(Color.BLACK);
				
				g.drawString("Great job!", 300, 75);//message, RP
				g.drawString("WPM: " + (int)wpm, 35, 170); // paint wpm, AG
				g.drawString("Acc: " + (int)acc + "%", 270, 170); // paint accuracy, AG

				//displays final time after level is completed, AG
				if((int)timeElapsed%60 < 10) // if seconds is one digit, print a zero before Ex: 2:04
				{
					g.drawString("Time: " + (int)timeElapsed/60+ ":0" + (int)timeElapsed%60, 535, 170);
				}
				else // else paint normally Ex: 2:42
				{
					g.drawString("Time: " + (int)timeElapsed/60+ ":" + (int)timeElapsed%60, 535, 170);
				}
			}
		}
		class EndPanel extends JPanel implements ActionListener, ChangeListener, AdjustmentListener//asks for user feedback, final panel of the game, RP
		{
			public EndPanel()
			{
				setLayout(null);
				setBackground(Color.GREEN);
				
				feedback = new JTextArea("Comment your thoughts about this game:", 5, 5);//allows user to comment their thoughts on Type Fight, RP
				add(feedback, BorderLayout.WEST);
				feedback.setVisible(true);
				
				submit = new JButton("Submit");//allows user to submit what was entered into the JTextArea, RP
				submit.setBounds(50, 310, 100, 50);
				add(submit);
				
				//allows user to scroll through what was typed in the text area, RP 
				JScrollPane scroll = new JScrollPane(feedback, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				feedback.setLineWrap(true);
				feedback.setWrapStyleWord(true);
				scroll.setBounds(0, 225, 200, 50);
				add(scroll, BorderLayout.WEST);
				scroll.setVisible(true);
				
				finish = new JButton("END THE GAME");//exits the game once clicked, RP
				finish.setBounds(600, 550, 200, 50); // set button position size
				add(finish);
				
				status = new JLabel("Show how much you liked the game:");//allows user to visually show on a scale how much they like the game, RP
				status.setBounds(50, 450, 300, 50);
				slider = new JSlider();
				add(slider);
				
				//paints ticks on the slider, RP
				slider.setMinorTickSpacing(10);
		        slider.setPaintTicks(true);
		         
		        // Set the labels to be painted on the slider, RP
		        slider.setPaintLabels(true);
		         
		        // Add positions label in the slider, RP
		        Hashtable<Integer, JLabel> position = new Hashtable<Integer, JLabel>();
		        position.put(0, new JLabel("I didn't like it"));
		        position.put(50, new JLabel("It was okay"));
		        position.put(100, new JLabel("I loved it!"));
		         
		        // Set the label to be drawn, RP
		        slider.setLabelTable(position);
				slider.addChangeListener(this);
				slider.setVisible(true);
				slider.setBounds(0, 500, 300, 50);
				add(status);
				}

			public void paintComponent(Graphics g)//prints out the final message/feedback in the End Panel, RP
			{
				super.paintComponent(g);
				g.setFont(font1);
				g.drawString("Did you like the game(yes/no)?", 0, 200);
				
				g.setFont(font2);
				g.drawString("Congratulations!!!", 0, 100);
				g.drawString("You have finished Type Fight!!!", 0, 150);
				g.drawString("Click the Button to Exit!", 0, 400);
				
				g.setFont(font1);
				g.drawString("Well, thanks for the feedback!", 400, 200);
			}

			public void actionPerformed(ActionEvent e) 
			{
			}
			
			public void stateChanged(ChangeEvent e)//prints out a message based on the user's liking of the game, RP
			{
				if(((JSlider)e.getSource()).getValue()>50)
					status.setText("Glad you enjoyed the game!");	
				else if(((JSlider)e.getSource()).getValue()<50)
					status.setText("Sorry to hear that");
				else
					status.setText("Play again and you might like it.");	
			}
		
			public void adjustmentValueChanged(AdjustmentEvent e) 
			{
			}
			
		}
	}
}