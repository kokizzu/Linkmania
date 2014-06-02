package game;

import game.LinkMania.GameHandler.XArena;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;

public class LinkMania extends JFrame
	{
	static final long serialVersionUID=1;
	public FileHandler fh=null;
	public TimeHandler th=null;
	public MouseHandler mh=null;
	public KeyHandler kh=null;
	public AnimHandler ah=null;
	public IntelHandler ih=null;
	public DrawHandler dh=null;
	public GameHandler gh=null;
	public Random rh=new Random();	
	public Component oh=this;
	
	public LinkMania(int nw,int nh,boolean AI)
		{
		super(" LinkMania "+0.3);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		gh=new GameHandler(nw,nh);
		fh=new FileHandler("image");
		th=new TimeHandler();
		ah=new AnimHandler();
		if(AI)
			{
			ih=new IntelHandler();
			}
		
		Insets fi=getInsets();
		final Rectangle fr=new Rectangle(0,0,nw+fi.left+fi.right,nh+fi.top+fi.bottom);
		setBounds(fr);		
		Container fc=getContentPane();
		fc.setLayout(null);
		fc.add((dh=new DrawHandler(fi,new Rectangle(2,2,nw,nh))));		
		setResizable(false);
		setFocusTraversalKeysEnabled(false);

		kh=new KeyHandler();
		mh=new MouseHandler();		
		addKeyListener(kh);
		addMouseListener(mh);
		addMouseMotionListener(mh);

		gh.arena[0].recurseAll();
		gh.arena[1].recurseAll();
		
		th.start();
		ah.start();
		if(AI)
			{
			ih.start();
			}
		dh.run();
		}
	
	public class GameHandler
		{
		public int state=1;
		public BufferedImage db=null;
		public Graphics2D dbg2d=null;
		public XArena arena[]=
			{
      new XArena(20,320),
      new XArena(580,320)
      };
		public XArena dupl=null;
	
		public GameHandler(int nw,int nh)
			{			
			db=new BufferedImage(nw,nh,BufferedImage.TYPE_INT_RGB);
			dbg2d=(Graphics2D)db.getGraphics();
			dbg2d.setColor(Color.white);
			dbg2d.setFont(new Font("Verdana",Font.PLAIN,11));
			}				
		public class XArena
			{
			public XTile[][] tiles=null;
			public final int tsize=9;
			public final int tdim=40;
			public long scorenow=0;
			public long scoreto=0;
			public long[] scorelinks={0,0,0};
			public int[] scorecombo={0,0,0};
			public long scoreaverage=0;
			public long scorebest=0;
			public int xpos=0;
			public int ypos=0;
			public int xcur=0;
			public int ycur=0;
			public boolean zcur=false;
			public final int xmove[]={ 0,+1, 0,-1};
			public final int ymove[]={-1, 0,+1, 0};
			public int totalrec=0;
			
			public XArena(int xp,int yp)
				{
				tiles=new XTile[tsize][tsize];
				for(int zxc=0;zxc<tsize;++zxc)
					{
					for(int xcv=0;xcv<tsize;++xcv)
						{
						tiles[zxc][xcv]=new XTile();
						}
					}
				xpos=xp;
				ypos=yp;
				}			
			public XArena(XArena copy)
				{
				tiles=new XTile[tsize][tsize];
				for(int zxc=0;zxc<tsize;++zxc)
					{
					for(int xcv=0;xcv<tsize;++xcv)
						{
						tiles[zxc][xcv]=new XTile(copy.tiles[zxc][xcv]);
						}
					}
				}
			public class XTile
				{
				public XProp pipes=null;
				public XProp links=null;
				public XProp power=null;
				public int index=0;
				public int type=0;
				public int score=0;
				public int anim=0;
				public int danim=1;
				public boolean mark=false;
				
				public XTile()
					{
					pipes=new XProp();
					links=new XProp();
					power=new XProp();
					type=rh.nextInt(250);
					if(type<200)
						{
						type=type/20;
						score=2;
						}
					else if(type<220)
						{
						type=11+(type-200)/5;
						score=4;
						}
					else
						{
						type=10;
						score=1;
						}			
					anim=rh.nextInt(5);
					xpipe();
					xindex();
					}
				public XTile(XTile copy)
					{
					pipes=new XProp(copy.pipes);
					links=new XProp(copy.links);
					power=new XProp(copy.power);
					type=copy.type;
					score=copy.score;
					xpipe();
					}
				public class XProp
					{
					public boolean[] prop=null; 	
					public int bitfield=0; 
					public int count=0; 					
					public XProp()
						{
						prop=new boolean[4];
						reset();
						}
					public XProp(XProp copy)
						{
						prop=new boolean[4];
						set(copy.prop);
						}
					public void set(boolean[] p)
						{
						bitfield=0;
						count=0;
						for(int zxc=0;zxc<4;++zxc)
							{
							if(prop[zxc]=p[zxc])
								{
								++count;
								bitfield|=(1<<zxc);
								}
							}						
						}
					public int refresh()
						{
						bitfield=0;
						count=0;
						for(int zxc=0;zxc<4;++zxc)
							{
							if(prop[zxc])
								{
								++count;
								bitfield|=(1<<zxc);
								}
							}			
						return(count);
						}
					public void reset()
						{
						bitfield=0;
						count=0;
						for(int zxc=0;zxc<4;++zxc)
							{
							prop[zxc]=false;
							}
						}
					}
				public void xpipe()
					{
					switch(type)
						{
						case 0:	pipes.set(new boolean[]{false,true,true,false});break;
						case 1:	pipes.set(new boolean[]{false,false,true,true});break;
						case 2:	pipes.set(new boolean[]{true,false,false,true});break;
						case 3:	pipes.set(new boolean[]{true,true,false,false});break;
						case 4:	pipes.set(new boolean[]{false,true,false,true}); break;
						case 5:	pipes.set(new boolean[]{true,false,true,false});break;
						case 6:	pipes.set(new boolean[]{true,true,false,true}); break;
						case 7:	pipes.set(new boolean[]{true,true,true,false});break;
						case 8:	pipes.set(new boolean[]{false,true,true,true}); break;
						case 9:	pipes.set(new boolean[]{true,false,true,true}); break;
						case 10:pipes.set(new boolean[]{true,true,true,true}); break;
						case 11:pipes.set(new boolean[]{false,false,true,false});break;
						case 12:pipes.set(new boolean[]{false,false,false,true}); break;
						case 13:pipes.set(new boolean[]{true,false,false,false});break;
						case 14:pipes.set(new boolean[]{false,true,false,false});break;
						case 15:pipes.set(new boolean[]{false,false,false,false});break;
						}		
					}
				public void xanimate()
					{				
					anim+=danim;
					if(anim<0)
						{
						danim=1;
						anim=0;
						}
					else if(anim>4)
						{
						danim=-1;
						anim=4;
						}
					xindex();
					}
				public int xindex()
					{
					return(index=power.bitfield*80+type*5+anim);
					}
				public void xrotateL()
					{
					switch(type)
						{
						case 0:case 1:case 2:case 3:
							type=(type+3)%4;
							break;
						case 4:
							type=5;
							break;
						case 5:
							type=4;
							break;
						case 6:case 7:case 8:case 9:
							type=(type-6+3)%4+6;
							break;
						case 11:case 12:case 13:case 14:
							type=(type-11+3)%4+11;
							break;
						}				
					xpipe();
					xindex();
					}
				public void xrotateR()
					{
					switch(type)
						{
						case 0:case 1:case 2:case 3:
							type=(type+1)%4;
							break;
						case 4:
							type=5;
							break;
						case 5:
							type=4;
							break;
						case 6:case 7:case 8:case 9:
							type=(type-6+1)%4+6;				
							break;
						case 11:case 12:case 13:case 14:
							type=(type-11+1)%4+11;					
							break;
						}
					xpipe();
					xindex();
					}
				}
			public void moveL()
				{
				zcur=true;
				xcur=(xcur+tsize-1)%tsize;
				}
			public void moveR()
				{
				zcur=true;
				xcur=(xcur+1)%tsize;
				}
			public void moveU()
				{
				zcur=true;
				ycur=(ycur+tsize-1)%tsize;
				}
			public void moveD()
				{
				zcur=true;
				ycur=(ycur+1)%tsize;
				}
			public void moveMouse(int mx,int my)
				{	
				zcur=false;
				if(mx>xpos && my>ypos)
					{
					mx-=xpos;
					my-=ypos;
					int mz=tdim*tsize;
					if(mx<mz && my<mz)
						{
						xcur=mx/tdim;
						ycur=my/tdim;
						zcur=true;
						}
					}			
				
				}
			public void rotateL()
				{
				if(zcur)
					{
					tiles[xcur][ycur].xrotateL();
					recurseAll();
					}
				else
					{
					eliminateAll();
					}
				}			
			public void rotateR()
				{
				if(zcur)
					{
					tiles[xcur][ycur].xrotateR();
					recurseAll();
					}
				else
					{
					eliminateAll();
					}
				}
			public void markAll(boolean state)
				{
				XTile tref=null;
				if(state)
					{
					for(int zxc=0;zxc<tsize;++zxc)
						{
						for(int xcv=0;xcv<tsize;++xcv)
							{
							tref=tiles[zxc][xcv];
							if(tref.mark)
								{
								tref.power.set(tref.links.prop);
								}
							else
								{								
								tref.power.reset();
								}
							tref.xindex();
							tref.mark=false;
							}
						}
					}
				else
					{
					for(int zxc=0;zxc<tsize;++zxc)
						{
						for(int xcv=0;xcv<tsize;++xcv)
							{
							tiles[zxc][xcv].mark=false;
							tiles[zxc][xcv].links.reset();
							}
						}
					}
				}
			public void eliminateAll()
				{
				XTile tref=null;
				long combo=0;
				long scoreadd=0;
				int maxpower=1;
				scorenow=scoreto;
				for(int zxc=0;zxc<tsize;++zxc)
					{
					for(int xcv=0;xcv<tsize;++xcv)
						{
						tref=tiles[zxc][xcv];
						int power=tref.power.count;
						if(power>1)
							{
							scoreadd+=tref.score*power;
							scorelinks[power-2]+=tref.score*power;
							++scorecombo[power-2];
							++combo;
							if(power>maxpower)
								{
								maxpower=power;
								}
							tiles[zxc][xcv]=new XTile();
							}
						}
					}
				if(combo!=0)
					{
					scoreadd+=combo*maxpower;
					if(scoreadd*5>scorebest)
						{
						scorebest=scoreadd*5;
						}
					scoreto+=scoreadd;
					System.gc();
					recurseAll();
					}
				}
			public int recurseAll()
				{
				totalrec=0;
				markAll(false);
				for(int zxc=0;zxc<tsize;++zxc)
					{
					if(tiles[zxc][0].pipes.prop[0])
						{
						++totalrec;
						recurseOne(zxc,0,2,0);
						}
					if(tiles[tsize-1][zxc].pipes.prop[1])
						{
						++totalrec;
						recurseOne(tsize-1,zxc,3,1);
						}
					if(tiles[zxc][tsize-1].pipes.prop[2])
						{
						++totalrec;
						recurseOne(zxc,tsize-1,0,2);
						}
					if(tiles[0][zxc].pipes.prop[3])
						{
						++totalrec;
						recurseOne(0,zxc,1,3);
						}
					}
				markAll(true);
				repaint();
				return(totalrec);
				}
			public void recurseOne(int x,int y,int from,int port)
				{
				int source=(from+2)%4;
				XTile tref=tiles[x][y];
				if(tref.pipes.prop[source] && !tref.links.prop[port])
					{
					tref.links.prop[port]=true;
					tref.power.prop[port]=true;
					tref.mark=true;
					//totalrec+=tref.power.refresh(); // best case but slower.. T__T
					++totalrec;
					for(int zxc=0;zxc<4;++zxc)
						{
						if(zxc!=source && tref.pipes.prop[zxc])
							{
							int nx=x+xmove[zxc];
							int ny=y+ymove[zxc];
							if(nx>=0 && ny>=0 && nx<tsize && ny<tsize)
								{
								if(tiles[nx][ny].pipes.prop[(zxc+2)%4])
									{
									++totalrec;
									recurseOne(nx,ny,zxc,port);
									}
								}
							}
						}
					}
				}
			public void updateScore()
				{
				if(scorenow<scoreto)
					{
					++scorenow;
					}
				}
			public void animateAll()
				{
				// versi matrix
				for(int zxc=0;zxc<tsize;++zxc)
					{
					tiles[((int)th.runtime+zxc)%tsize][(dh.fcur+rh.nextInt(99))%tsize].xanimate();
					}
				// versi random
				/*
				int count=tsize+rh.nextInt(tsize);
				while(count>0)
					{
					tiles[rh.nextInt(tsize)][rh.nextInt(tsize)].xanimate();
					--count;
					}
				*/
				}
			}
		public XArena duplicate(int pindex)
			{
			dupl=new XArena(arena[pindex]);
			System.gc();
			return(dupl);
			}
		}

	public class ErrorHandler
		{
		public ErrorHandler(Exception e,String mesg)
			{
			new ErrorMessage(mesg+" "+e);
			System.exit(0);
			}
		public ErrorHandler(Exception e)
			{
			try
				{
				fh.doslog.writeBytes("Exception : "+e);
				}
			catch(Exception err)
				{
				new ErrorHandler(err," cannot handle multiple exception.. ");
				}
			}
		
		public class ErrorMessage
			{		
			public ErrorMessage(String mesg)
				{
				JOptionPane.showMessageDialog(oh,mesg," Error",JOptionPane.ERROR_MESSAGE);
				}		
			}
		}
	
	public class AnimHandler extends Thread
		{		
		public AnimHandler()
			{		
			}		
		public void run()
			{			
			while(gh.state>0)
				{
				try
					{
					Thread.sleep(100);
					gh.arena[0].animateAll();
					gh.arena[1].animateAll();
					}				
				catch(Exception e)
					{
					new ErrorHandler(e);
					}
				}			
			}		
		}

	public class TimeHandler extends Thread
		{
		public long runtime=1;
		
		public TimeHandler()
			{
			}
		public void run()
			{
			while(gh.state>0)
				{
				try
					{
					Thread.sleep(1000);
					dh.fps();
					++runtime;
					XArena aref=gh.arena[0];
					aref.scoreaverage=aref.scoreto/runtime;
					aref=gh.arena[1];
					aref.scoreaverage=aref.scoreto/runtime;
					}
				catch(Exception e)
					{
					new ErrorHandler(e);
					}
				}
			}
		}
	
	public class IntelHandler extends Thread
		{
		public final int pindex=0;
		public int lastx=0;
		public int lasty=0;
		public int lastz=-1;
		public int lastv=0;
		
		public IntelHandler()
			{			
			reset();
			}
		public void run()
			{
			while(gh.state>0)
				{
				try
					{
					Thread.sleep(125);
					solution();
					}
				catch(Exception e)
					{
					new ErrorHandler(e);
					}
				}
			}
		public void reset()
			{
			lastx=0;
			lasty=0;
			lastz=-1;
			}
		public void solution()
			{
			switch(lastz)
				{
				case -1:
					{
					game.LinkMania.GameHandler.XArena aref=gh.duplicate(pindex);
					game.LinkMania.GameHandler.XArena.XTile tref=null;
					int oldx=lastx,oldy=lasty;
					int bestv=-1;
					int bestx=-1,besty=-1,bestz=0;
					for(int zxc=0;zxc<aref.tsize;++zxc)
						{
						for(int xcv=0;xcv<aref.tsize;++xcv)
							{
							tref=aref.tiles[zxc][xcv];	
							for(int cvb=0;cvb<3;++cvb)
								{									
								tref.xrotateL();
								int rec=aref.recurseAll();
								if(bestv<rec)
									{
									bestv=rec;
									bestx=zxc;
									besty=xcv;
									bestz=cvb;
									}
								}
							tref.xrotateL();
							}
						}
					if(bestv!=-1)
						{
						lastx=bestx;
						lasty=besty;
						lastz=bestz;
						lastv=bestv;
						}
					if(oldx==lastx && oldy==lasty)
						{
						lastx=0;
						lasty=0;
						lastz=-2;						
						}
					}
					break;
				case -2:
					{
					game.LinkMania.GameHandler.XArena aref=gh.arena[pindex];
					aref.eliminateAll();
					reset();
					}
					break;
				default:
					{
					game.LinkMania.GameHandler.XArena aref=gh.arena[pindex];
					if(aref.xcur<lastx)
						{
						aref.moveR();
						}
					else if(aref.xcur>lastx)
						{
						aref.moveL();
						}
					else
						{
						if(aref.ycur<lasty)
							{
							aref.moveD();
							}
						else if(aref.ycur>lasty)
							{
							aref.moveU();
							}
						else
							{
							if(lastz==3)
								{
								aref.rotateR();
								lastz=0;
								}
							else if(lastz>=0)
								{
								aref.rotateL();
								--lastz;
								}
							}
						}
					}
				}
			}
		}	

	public class FileHandler
		{
		public DataOutputStream doslog;		
		public Image[] im;
		public final int imtotal=1281;
		
		public FileHandler(String dir)
			{			
			dir+=File.separator;
			try
				{
				doslog=new DataOutputStream(new FileOutputStream(new File("error.log")));
				try
					{					
					im=new Image[imtotal];
					for(int zxc=0;zxc<imtotal-1;++zxc)
						{
            switch(zxc/80)
              { 
              case 11:
              case 13:
              case 14:
                im[zxc]=im[7*80+zxc%80];
                break;
              default:
                im[zxc]=ImageIO.read(new File(dir+(zxc/80)+File.separator+"X"+(zxc%80/5)+" ("+(zxc%5)+").bmp"));
                break;
              }
						}
					im[imtotal-1]=ImageIO.read(new File(dir+"back.bmp"));
					}
				catch(Exception e)
					{
					new ErrorHandler(e," cannot load image file.. ");
					}
				}
			catch(Exception e)
				{
				new ErrorHandler(e," cannot open/write log file.. ");
				}
			}
		public void log(String mesg)
			{
			try
				{
				doslog.writeBytes(mesg);
				}
			catch(Exception e)
				{
				new ErrorHandler(e);
				}
			}
		}
	
	public class DrawHandler extends JPanel implements Runnable
		{
		static final long serialVersionUID=2;
		public int fcur=0;
		public int flast=0;
		public Insets fri=null;
		
		public DrawHandler(Insets fr,Rectangle b)
			{
			setLayout(null);
			setBounds(b);
			setOpaque(false);
			fri=fr;
			}
		public void run()	
			{			
			while(gh.state>0)
				{
				try	
					{
					Thread.sleep(50);
					repaint();
					}
				catch(Exception e)
					{
					new ErrorHandler(e);
					}			
				}			
			}				
		public void paint(Graphics g)
			{			
			++fcur;
			Graphics2D g2d=(Graphics2D)g;		
			gh.dbg2d.drawImage(fh.im[fh.imtotal-1],null,null);
			
			game.LinkMania.GameHandler.XArena.XTile tref=null;
			
			int mx=0,my=0;
			if(ih!=null)
				{
				gh.dbg2d.drawString(" v"+ih.lastv+" x"+ih.lastx+" y"+ih.lasty+" z"+ih.lastz,50,75);
				}
			for(int zxc=0;zxc<2;++zxc)
				{
				game.LinkMania.GameHandler.XArena aref=gh.arena[zxc];
				for(int xcv=0;xcv<3;++xcv)
					{
					gh.dbg2d.drawString("Score / Combo "+(xcv+2)+" : "+Long.toString(aref.scorelinks[xcv]*5)+" / "+(aref.scorecombo[xcv]*5),aref.xpos+150,30+xcv*15);
					}
				gh.dbg2d.drawString("Best / Average   : "+aref.scorebest+" / "+aref.scoreaverage,aref.xpos+150,75);
				aref.updateScore();
				gh.dbg2d.drawString(""+5*aref.scorenow,aref.xpos,50);
				long diff=aref.scoreto-aref.scorenow;
				if(diff>0)
					{
					gh.dbg2d.drawString("+"+5*diff,aref.xpos+10,60);
					}
				int dim=aref.tdim;
				for(int xcv=0;xcv<aref.tsize;++xcv)
					{
					for(int cvb=0;cvb<aref.tsize;++cvb)
						{
						tref=aref.tiles[xcv][cvb];
						mx=aref.xpos+xcv*dim;
						my=aref.ypos+cvb*dim;
						gh.dbg2d.drawImage(fh.im[tref.index],mx,my,null);
						}
					}
				mx=aref.xcur*dim+aref.xpos;
				my=aref.ycur*dim+aref.ypos;
				gh.dbg2d.drawRect(mx,my,dim,dim);
				}
						
			g2d.drawImage(gh.db,null,null);
			
			g2d.setFont(new Font("Verdana",Font.PLAIN,14));			
			g2d.setColor(Color.white);
			g.drawString(dh.flast+" FPS",908,718);	
			}
		public void fps()
			{
			flast=fcur;
			fcur=0;
			}
		}	

	public class KeyHandler implements KeyListener
		{
		public final int pindex=0;
		public KeyHandler()
			{
			}
		public void keyPressed(KeyEvent ke)
			{
			switch(ke.getKeyCode())
				{
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A:
					gh.arena[pindex].moveL();
					break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D:
					gh.arena[pindex].moveR();
					break;
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W:
					gh.arena[pindex].moveU();
					break;
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S:
					gh.arena[pindex].moveD();
					break;
				case KeyEvent.VK_COMMA:
					gh.arena[pindex].rotateL();
					break;
				case KeyEvent.VK_PERIOD:
					gh.arena[pindex].rotateR();
					break;
				case KeyEvent.VK_SPACE:
				case KeyEvent.VK_SLASH:
					gh.arena[pindex].eliminateAll();
					break;
				}
			}
		public void keyReleased(KeyEvent ke)
			{
			}
		public void keyTyped(KeyEvent ke)
			{
			}
		}
	
	public class MouseHandler implements MouseListener,MouseMotionListener
		{		
		public final int pindex=1;
		public MouseHandler()
			{
			}	
		public void mousePressed(MouseEvent me)
			{
			switch(me.getButton())
				{
				case MouseEvent.BUTTON1:
					gh.arena[pindex].rotateL();
					break;
				case MouseEvent.BUTTON2:
					gh.arena[pindex].eliminateAll();
					break;
				case MouseEvent.BUTTON3:
					gh.arena[pindex].rotateR();
					break;
				}
			}	
		public void mouseReleased(MouseEvent me)
			{
			}	
		public void mouseEntered(MouseEvent me)
			{
			}	
		public void mouseExited(MouseEvent me)
			{
			}	
		public void mouseClicked(MouseEvent me)
			{
			}	
		public void mouseDragged(MouseEvent me)
			{
			}	
		public void mouseMoved(MouseEvent me)
			{
			gh.arena[pindex].moveMouse(me.getX()-dh.fri.left,me.getY()-dh.fri.top);
			}
		}
	
	public static void main(String[] args)
		{
		boolean useAI=false;
		if(args.length>=1)
			{
			for(int zxc=0;zxc<args.length;++zxc)
				{
				if(args[zxc].compareToIgnoreCase("AI")==0)
					{
					useAI=true;
					}
				}
			}
		new LinkMania(960,720,useAI);
		}

	}
