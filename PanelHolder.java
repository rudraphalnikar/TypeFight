/* 
Abhijit Gottumukkala, Rudra Phalnikar
April 22, 2019
PanelHolder.java
GAME
 */


import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Scanner;
import java.io.*;
import javax.imageio.ImageIO;

public class PanelHolder extends JPanel implements ActionListener, KeyListener
{
	JMenuBar j;

	TitleScreen ts;
	Instructions i;
	GamePanelHolder gph;

	GamePanel gp;
	TopPanelHolder tph;

	ResultsPanel rp;
	InfoPanel ip;

	Timer t;

	//TitleScreen components
	private JButton help, start;

	//Instructions components
	private JButton home;
	private	String imageName;
	private	Image image;

	//GamePanel components
	private int numWrong, numRight, charNum, lineNum, lessonNum, numLevels;
	private Font font1, font2, font3;
	private double timeElapsed, acc, wpm;
	private boolean correct, levelFinished;

	//ResultsPanel components
	private JButton next, retry, skip;

	private Scanner in; // read from file

	private String [][] line; // lines to be typed from text file

	public PanelHolder()
	{
		setPreferredSize(new Dimension(800, 600)); // size of panel
		setLayout(new CardLayout()); 



		ts = new TitleScreen();
		i = new Instructions();
		gph = new GamePanelHolder();


		help.addActionListener(this);
		start.addActionListener(this);

		home.addActionListener(this);

		retry.addActionListener(this);
		next.addActionListener(this);
		skip.addActionListener(this);

		t = new Timer(100, this);

		add(ts, "TS"); // add panels to cardlayout, adding title screen first to make it show up at start
		add(i, "I");
		add(gph, "GPH");

		addKeyListener(this);

		setFocusable(true);
		//		requestFocusInWindow();


		//		t.start();

	}

	public void actionPerformed(ActionEvent e)
	{

		if(e.getSource() == t) // to count time, increment time elapsed by 0.1 seconds every tenth of a second
		{
			timeElapsed += 0.1;

			wpm = (numRight+numWrong)/6/timeElapsed*60; // assuming average english word length including spaces is 6

			gp.repaint();
		}
		else if(e.getSource() == help) // move to instruction panel
		{
			CardLayout cl = (CardLayout)(getLayout());
			cl.show(this, "I");
		}
		else if(e.getSource() == home) // move to title screen panel
		{
			CardLayout cl = (CardLayout)(getLayout());
			cl.show(this, "TS");
		}
		else if(e.getSource() == start) // move to game panel when user presses start
		{
			CardLayout cl = (CardLayout)(getLayout());
			cl.show(this, "GPH");

			t.start(); //  start timer when user enters the game panel
		}
		else if(e.getSource() == next)
		{
			CardLayout cl = (CardLayout)(tph.getLayout());
			cl.show(tph, "IP");

			requestFocusInWindow();
			
			levelFinished = false;
			
			lessonNum++;

			lineNum = 0;
			charNum = 0;

			wpm = 0;
			acc = 0;
			numWrong = 0;
			numRight = 0;

			timeElapsed = 0;
			
			t.restart();
		}
		else if(e.getSource() == retry)
		{
			CardLayout cl = (CardLayout)(tph.getLayout());
			cl.show(tph, "IP");

			requestFocusInWindow();
			
			levelFinished = false;
			
			lineNum = 0;
			charNum = 0;

			wpm = 0;
			acc = 0;
			numWrong = 0;
			numRight = 0;

			timeElapsed = 0;
			
			t.restart();
		}
		else if(e.getSource()== skip)
		{
			CardLayout cl = (CardLayout)(tph.getLayout());
			cl.show(tph, "IP");

			requestFocusInWindow();
			
			levelFinished = false;
			
			lineNum = 0;
			charNum = 0;

			wpm = 0;
			acc = 0;
			numWrong = 0;
			numRight = 0;

			timeElapsed = 0;
			
			t.restart();
			
			lessonNum+=5;
		}


	}

	public void keyTyped(KeyEvent e)
	{
		if(!levelFinished)
		{

			if(e.getKeyChar() == line[lessonNum][lineNum].charAt(charNum)) // if user types correct character
			{		
				if(correct)
				{
					numRight++; // increment
				}

				correct = true; 

				charNum++; // move to next letter
			}
			else
			{
				if(correct) // so only the first time a certain letter is typed incorrectly is it counted (if user doesnt realize they made a mistake & keep typing, accuracy doesnt drop to 0)
				{
					numWrong++;
				}
				correct = false;

				charNum++;
			}

			if(charNum == line[lessonNum][lineNum].length()) // if the line is finished
			{
				charNum = 0; // reset charNum
				lineNum++; // increase lineNum 
			}

			if(line[lessonNum][lineNum] == null) // if the last line of the lesson is finished
			{

				levelFinished = true;

				CardLayout cl = (CardLayout)(tph.getLayout());
				cl.show(tph, "RP");
				
				if((acc>=90&&wpm>=15) && (lessonNum == 1 || lessonNum == 7 || lessonNum == 13 || lessonNum == 19))
				{
					skip.setVisible(true);
				}
				else
				{
					skip.setVisible(false);
				}
				
				t.stop();
				
				
			}

		}

		acc = (float)numRight/(numRight+numWrong)*100; // divide the number correct by the total typed to get the accuracy

		gp.repaint();
	}

	public void keyPressed(KeyEvent e){}
	public void keyReleased(KeyEvent e){}

	class TitleScreen extends JPanel
	{	
		public TitleScreen()
		{
			setBackground(Color.RED);

			setLayout(new GridLayout(3, 1));		

			TopP top = new TopP(); // segmenting screen to allow for better component placement
			JPanel middle = new JPanel();
			JPanel bottom = new JPanel();

			middle.setBackground(Color.RED);
			bottom.setBackground(Color.RED);

			help = new JButton("Help");
			start = new JButton("Start");

			help.setPreferredSize(new Dimension(150, 150)); // set button size
			start.setPreferredSize(new Dimension(150, 150));

			bottom.add(help);
			bottom.add(start);

			add(top);
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

				g.drawString("Type Fight", 310, 50);
			}
		}

	}

	class Instructions extends JPanel
	{

		public Instructions()
		{
			setLayout(null);
			setBackground(Color.CYAN);

			home = new JButton("Back to Home Screen");

			home.setBounds(600, 550, 200, 50); // set button position size

			add(home);
			
			image = null;
			imageName = "instructions.jpg";
			getMyImage();

		}
		
		public void getMyImage() 
		{
			File imageFile = new File("src/"+imageName);
			try
			{
				image =  ImageIO.read(imageFile);
			}
			catch(IOException ioe)
			{
				System.err.println("\n\n\n"+imageName+" cannot be found.");
				ioe.printStackTrace();
			}
		}

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			g.drawImage(image, 0, 0, this);
			Font title = new Font("Serif", Font.BOLD, 40);
			g.setFont(title);

			g.setColor(Color.BLACK);

			g.drawString("Instructions", 280, 50);

			Font instructions = new Font("Serif", Font.PLAIN, 20);
			g.setFont(instructions);

			g.drawString("Learn to type in different section that correspond to each key(s).", 0, 150);
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

	class GamePanelHolder extends JPanel
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


			wpm = 0;
			acc = 0;
			numWrong = 0;
			numRight = 0;

			timeElapsed = 0;

			charNum = 0;
			lineNum = 0;
			lessonNum = 0;

			line = new String[100][100];

			read(); // copy file to array

			font1 = new Font("Serif", Font.PLAIN, 25);
			font2 = new Font("Serif", Font.BOLD, 40);
		}

		public void read()
		{
			int x = 0;
			String temp;

			numLevels = 0;


			File inFile = new File("src/text.txt");

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

			for(int x = 0; x < 10; x++) // grid to help position components (to be removed later)
			{
				g.drawLine(100*x, 0, 100*x, 1000);
				g.drawLine(0, 100*x, 1000, 100*x);
			}

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


			if((int)timeElapsed%60 < 10) // if seconds is one digit, print a zero before Ex: 2:04
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
				if(line[lessonNum][lineNum+i] != null)							// if the line doesnt exist, dont paint it
					g.drawString(line[lessonNum][lineNum+i], 220, i*30+40);
			}

			if(correct) // if typed correctly, paint in green
			{
				g.setColor(Color.GREEN);
			}
			else // if incorrect, paint in red
			{
				g.setColor(Color.RED);
			}

			if(line[lessonNum][lineNum] != null) // print out the highlight to show what the user has typed
				g.drawString(line[lessonNum][lineNum].substring(0 ,charNum), 220, 40);
		}	
	}

	class TopPanelHolder extends JPanel
	{
		public TopPanelHolder()
		{
			setPreferredSize(new Dimension(800, 350));
			setLayout(new CardLayout());

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

	class InfoPanel extends JPanel
	{
		public InfoPanel()
		{
			setBackground(Color.BLUE);
		}

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
		}
	}

	class ResultsPanel extends JPanel
	{
		public ResultsPanel()
		{
			setBackground(Color.GREEN);

			next = new JButton("Next Level");
			retry = new JButton("Retry Level");
			skip = new JButton("Skip 5 Levels");

			add(retry);
			add(next);
			add(skip);
			skip.setVisible(false);
			
			font3 = new Font("Serif", Font.BOLD, 45);
		}

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			g.setFont(font3);
			g.setColor(Color.BLACK);
			
			g.drawString("Great job!", 300, 75);
			
			g.drawString("WPM: " + (int)wpm, 35, 170); // paint wpm
			g.drawString("Acc: " + (int)acc + "%", 270, 170); // paint accuracy


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

}