/**
 * Author: Nana
 * Course: CAP 3027
 * Term Project
 */


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class QuizShow {

    private static final int WIDTH = 1048;
    private static final int HEIGHT = 822;

    public static void main( String[] args )
    {
        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
        } catch ( InstantiationException e ) {
            e.printStackTrace();
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        } catch ( UnsupportedLookAndFeelException e ) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                showGUI();
            }
        });
    }

    public static void showGUI()
    {
        JFrame frame = new GameFrame( WIDTH, HEIGHT );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
        frame.setResizable(false);
    }
}

class GameFrame extends JFrame
{

    private Players[] players = new Players[4];
    private Timer fadeOut;
    private Timer fadeIn;
    private boolean isRunning_fadeIn;
    private boolean isRunning_fadeOut;
    private boolean firstRound;
    private MainWindow window;
    private int active_player;
    private File file;
    private int num_of_q;
    private int BigBlind;
    private int SmallBlind;
    private int Jackpot;
    private int index;
    private int firstPlayer;
    private int turns;
    private int current_players;
    private HUD GUI = new HUD();
    private JButton bCall = new JButton();
    private JButton bRaise = new JButton();
    private JButton bCheck = new JButton();
    private JButton bResponse = new JButton();
    private SpinnerNumberModel model;
    private QuestionSet[] questions;
    private BufferedImage board = null;



    public GameFrame( int width, int height )
    {
        this.setTitle("Quiz Show - Poker Style");
        this.setSize(width, height);
        this.setLayout(new BorderLayout());
        addMenu();
        startScreen();

    }

    /**
     * Creates a menu
     */
    private void addMenu()
    {

        JMenu fileMenu = new JMenu( "File" );

        JMenuItem exitItem = new JMenuItem( "Exit" );
        exitItem.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                System.exit( 0 );
            }

        }
        );

        fileMenu.add( exitItem );

        JMenuBar menuBar = new JMenuBar();
        menuBar.add( fileMenu );
        this.setJMenuBar( menuBar );

    }

    private void startScreen()
    {
        BufferedImage bg = null;
        String imgPath = "imgs/bg.png";

        try {
               JButton startButton = new JButton( "Start" );
               startButton.setBounds( 428,556,150,40 );
               bg = ImageIO.read( getClass().getResourceAsStream( imgPath ) );
               startButton.addActionListener( new ActionListener()
               {
                     public void actionPerformed( ActionEvent e ) {

                         newGame();

                   }
               });

            this.setContentPane( new JScrollPane ( new JLabel ( new ImageIcon ( bg ) ) ) );
            this.getContentPane().add( startButton );
            this.validate();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start a new game
     */
    public void newGame()
    {
        turns = 0;
        current_players = 4;
        String coins_string = JOptionPane.showInputDialog( "Number of starting coins[1000, 50000, 10000]:" );
        String bigblind_string = JOptionPane.showInputDialog( "Big Blind value [100-600]" );

        BigBlind = Integer.parseInt( bigblind_string );
        SmallBlind = BigBlind/2;

        Random rand = new Random();

        for(int i = 0; i < 4; i++)
        {
            players[i] = new Players( Integer.parseInt( coins_string ) );
        }

        window = new MainWindow( 1024,768 );

        initializeQBank();

        createAndShowGameBoard( this );

        startGame( this );
    }

    /**
     * Start Game. Set up who is the dealer and reveal for the first part of the question. Update the miniGui to
     * indicate whose turn it is. Set the call price. Listen for click button events
     * @param frame the game Frame.
     */
    public void startGame( final GameFrame frame )
    {
        Random rand = new Random();
        firstRound = true;
        index = 0;
        Jackpot = 0;

        //choose a dealer at random
        int choose_dealer = rand.nextInt( 4 );
        players[choose_dealer].setDealer( true );


        //Choose the BigBlind and SmallBlind players; picked from the left of Dealer
        //Set active player for turn and first player
        if(choose_dealer == 1)
        {
            players[choose_dealer+1].setIsSmallBlind( true );
            updateStatus( choose_dealer+1,"Small Blind" );
            players[choose_dealer+1].setFold( false );
            players[choose_dealer+2].setIsBigBlind( true );
            updateStatus( choose_dealer+2, "Big Blind" );
            players[choose_dealer+2].setFold( false );
            active_player = 0;
            firstPlayer = 0;
        }
        else if(choose_dealer == 2)
        {
            players[3].setIsSmallBlind( true );
            updateStatus( 3,"Small Blind" );
            players[3].setFold( false );
            players[0].setIsBigBlind( true );
            updateStatus( 0, "Big Blind" );
            players[0].setFold( false );
            active_player = 1;
            firstPlayer = 1;
        }
        else if(choose_dealer == 3)
        {
            players[0].setIsSmallBlind( true );
            updateStatus( 0, "Small Blind" );
            players[0].setFold( false );
            players[1].setIsBigBlind( true );
            updateStatus( 1, "Big Blind" );
            players[1].setFold( false );
            active_player = 2;
            firstPlayer = 2;
        }
        else
        {
            players[choose_dealer+1].setIsSmallBlind( true );
            updateStatus( choose_dealer+1, "Small Blind" );
            players[choose_dealer+1].setFold( false );
            players[choose_dealer+2].setIsBigBlind( true );
            updateStatus( choose_dealer+2, "Big Blind" );
            players[choose_dealer+2].setFold( false );
            active_player = choose_dealer+3;
            firstPlayer = active_player;

        }
           revealQuestionParts( frame );

           updateMiniGUI( active_player,frame );

           setCallPrice( BigBlind );
           bCall.setText( "Call: " + players[active_player].getCallPrice() );

           bCall.addActionListener( new ActionListener() {
                 @Override
                 public void actionPerformed(ActionEvent e) {
                     if(!isRunning_fadeIn && !isRunning_fadeOut)
                     {
                         // Retrieve chip  amount and call price for active player
                         // Set call price
                         int chip_amount = players[active_player].getChipAmount();
                         int callprice = players[active_player].getCallPrice();
                         setCallPrice(callprice);

                         // Change and update GUI and attributes for active player
                         bCall.setText("Call: " + callprice);
                         updateStatus(active_player, "Call");
                         players[active_player].setChipAmount(chip_amount-callprice);
                         updateMoney(active_player, chip_amount-callprice);
                         Jackpot+=callprice;
                         GUI.setJackPot(Jackpot);
                         endTurn(frame);
                     }

                 }
             });

        bRaise.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e ) {

                int callprice = players[active_player].getCallPrice();
                int chip_amount = players[active_player].getChipAmount();

                if( ( !isRunning_fadeIn && !isRunning_fadeOut ) && callprice < chip_amount )
                {
                      // open raise popup window
                      openRaiseDialog( frame );
                }
            }
        });


        bCheck.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e ) {

                if(!isRunning_fadeIn && !isRunning_fadeOut)
                {
                    endTurn( frame );
                }
            }
        });

        bResponse.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                    //open response popup window
                    openResponseDialog( frame );

            }
        });
    }

    /**
     * End turn and go to next player. If next player must pay SmallBlind amount take amount money away from player
     * and proceed to next player. If the next player must pay BigBlind amount take amount away from player and
     * proceed to next player.
     *
     * Update miniGUI and check to see if we are at the last turn of the round. If we are enter new round.
     * @param frame   the game Frame
     */
    public void endTurn( GameFrame frame )  {

        parseAvailablePlayers();
        updateMiniGUI( active_player,frame );
        bCall.setText("Call: " + players[active_player].getCallPrice());

        // Check if we are at the last turn of the round
        if( active_player ==  firstPlayer && !firstRound )
        {
            turns++;
            newRound( frame );

        }
        firstRound = false;
    }

    public void parseAvailablePlayers()
    {
        int chip_amount = 0;
        int handler = 0;
        int playerIndex = 0;

        if(active_player == 3)
        {
            active_player = 0;
            if(players[active_player].getSmallBlind())
            {

                players[active_player].setIsSmallBlind( false );
                chip_amount = players[active_player].getChipAmount();
                players[active_player].setChipAmount( chip_amount - SmallBlind );
                Jackpot+=SmallBlind;
                GUI.setJackPot( Jackpot );
                GUI.getPlayerUI( active_player ).updateMoneyLabel( chip_amount - SmallBlind );
                handler = active_player + 1;
                active_player = ( handler > 3 ) ? 0 : active_player + 1;
                if(players[active_player].getBigBlind())
                {
                    players[active_player].setIsBigBlind( false );
                    chip_amount = players[active_player].getChipAmount();
                    players[active_player].setChipAmount( chip_amount - BigBlind );
                    Jackpot+=BigBlind;
                    GUI.setJackPot( Jackpot );
                    GUI.getPlayerUI( active_player ).updateMoneyLabel( chip_amount - BigBlind );
                    handler = active_player + 1;
                    active_player = ( handler > 3 ) ? 0  : active_player + 1;

                }
            } else if(players[active_player].getBigBlind())
            {
                players[active_player].setIsBigBlind( false );
                chip_amount = players[active_player].getChipAmount();
                players[active_player].setChipAmount( chip_amount - BigBlind );
                Jackpot+=BigBlind;
                GUI.setJackPot(Jackpot);
                GUI.getPlayerUI( active_player ).updateMoneyLabel( chip_amount - BigBlind );
                handler = active_player + 1;
                active_player = ( handler > 3 ) ? 0  : active_player + 1;
                if(players[active_player].getSmallBlind())
                {
                    players[active_player].setIsSmallBlind( false );
                    chip_amount = players[active_player].getChipAmount();
                    players[active_player].setChipAmount( chip_amount - SmallBlind );
                    Jackpot+=SmallBlind;
                    GUI.setJackPot(Jackpot);
                    GUI.getPlayerUI( active_player ).updateMoneyLabel( chip_amount - SmallBlind );
                    handler = active_player + 1;
                    active_player = ( handler > 3 ) ? 0 : active_player + 1;
                }
            }

          while(!players[active_player].getAvailable())
           {
               System.out.println("SKIP NEG");
               active_player++;
               if(active_player > 3)
               {
                   active_player = 0;
               }
               if(active_player == firstPlayer)
               {
                   break;
               }
             //  active_player++;
          }
        }
        else {
            active_player++;

            if(players[active_player].getSmallBlind())
            {
                players[active_player].setIsSmallBlind( false );
                chip_amount = players[active_player].getChipAmount();
                players[active_player].setChipAmount( chip_amount - SmallBlind );
                Jackpot+=SmallBlind;
                GUI.setJackPot( Jackpot );
                GUI.getPlayerUI( active_player ).updateMoneyLabel( chip_amount - SmallBlind );
                handler = active_player + 1;
                active_player = ( handler > 3 ) ? 0 : active_player + 1;
                if(players[active_player].getBigBlind())
                {
                    players[active_player].setIsBigBlind( false );
                    chip_amount = players[active_player].getChipAmount();
                    players[active_player].setChipAmount( chip_amount - BigBlind );
                    Jackpot+=BigBlind;
                    GUI.setJackPot( Jackpot );
                    GUI.getPlayerUI( active_player ).updateMoneyLabel( chip_amount - BigBlind );
                    handler = active_player + 1;
                    active_player = ( handler > 3 ) ? 0  : active_player + 1;

                }

            } else if( players[active_player].getBigBlind() )
            {
                players[active_player].setIsBigBlind( false );
                chip_amount = players[active_player].getChipAmount();
                players[active_player].setChipAmount( chip_amount - BigBlind );
                Jackpot+=BigBlind;
                GUI.setJackPot( Jackpot );
                GUI.getPlayerUI(active_player).updateMoneyLabel( chip_amount - BigBlind );
                handler = active_player + 1;
                active_player = ( handler > 3 ) ? 0  : active_player + 1;

                if(players[active_player].getSmallBlind())
                {
                    players[active_player].setIsSmallBlind( false );
                    chip_amount = players[active_player].getChipAmount();
                    players[active_player].setChipAmount( chip_amount - SmallBlind );
                    Jackpot+=SmallBlind;
                    GUI.setJackPot( Jackpot );
                    GUI.getPlayerUI( active_player ).updateMoneyLabel( chip_amount - SmallBlind );
                    handler = active_player + 1;
                    active_player = ( handler > 3 ) ? 0 : active_player + 1;

                }

                while(!players[active_player].getAvailable())
                {
                    System.out.println("SKIP NEG");
                    if(active_player > 3)
                    {
                        active_player = 0;
                    }
                        if(active_player == firstPlayer)
                        {
                            break;//  active_player++;
                        }
                }

            }

        }

    }


    /**
     * Enter new round. Set up who will be the next dealer. The new dealer is the player at the left of the old dealer
     * from a previous round. Reveal part of new question. Increase BigBlind by 25% each new Round
     * @param frame
     */
    public void newRound( GameFrame frame )
    {
        int choose_dealer = -1;

        if( turns != 3 )
        {
            reportLosers();
            revealQuestionParts( frame );
        } else
        {
            revealWinners();

            BigBlind = (int) ( BigBlind * 1.50 );
            SmallBlind = BigBlind/2;

            updateStatus( -1,"???" );

            if(current_players > 2)
            {
                for(int i = 0; i < players.length; i++)
                {
                    if( players[i].getDealer() )
                    {
                        if( i == players.length-1 )
                        {
                            choose_dealer = 0;
                        }
                        else
                        {
                            choose_dealer = i++;
                            break;
                        }
                    }
                }
                players[choose_dealer].setDealer( true );

            }


            if(choose_dealer == 0)
            {
                players[choose_dealer+1].setIsSmallBlind( true );
                updateStatus( choose_dealer+1, "Small Blind" );
                players[choose_dealer+1].setFold( false );
                players[choose_dealer+2].setIsBigBlind( true );
                updateStatus( choose_dealer+2, "Big Blind" );
                players[choose_dealer+2].setFold( false );
                active_player = choose_dealer+3;
            }
            else if(choose_dealer == 1)
            {
                players[choose_dealer+1].setIsSmallBlind( true );
                updateStatus( choose_dealer+1, "Small Blind" );
                players[choose_dealer+1].setFold( false );
                players[choose_dealer+2].setIsBigBlind( true );
                updateStatus( choose_dealer+2,"Big Blind" );
                players[choose_dealer+2].setFold( false );
                active_player = 0;
            } else if( choose_dealer == 2 )
            {
                players[3].setIsSmallBlind( true );
                updateStatus( 3, "Small Blind" );
                players[3].setFold( false );
                players[0].setIsBigBlind( true );
                updateStatus( 0, "Big Blind" );
                players[0].setFold( false );
                active_player = 1;
            } else if( choose_dealer == 3 )
            {
                players[0].setIsSmallBlind( true );
                updateStatus( 0, "Small Blind" );
                players[0].setFold( false );
                players[1].setIsBigBlind( true );
                updateStatus( 1, "Big Blind" );
                players[1].setFold( false );
                active_player = 2;
            }

            turns = 0;

            revealQuestionParts( frame );
            setCallPrice( BigBlind );
            bCall.setText( "Call: " + players[active_player].getCallPrice() );
            bCheck.setText( "Check" );
            updateMiniGUI( active_player,frame );
        }

        endGame( frame );


    }

    /**
     *  Determine if this is the end of the game. If it is bring up Game Over popwindow
     * @param frame
     */
    public void endGame( GameFrame frame )
    {
         if( current_players < 2 )
         {
             Object[] options = {"Yes", "No"};
             int n = JOptionPane.showOptionDialog( frame,
                     "Would you like to restart? Pressing No will exit the program",
                     "Game Over",
                     JOptionPane.YES_NO_OPTION,
                     JOptionPane.QUESTION_MESSAGE,
                     null,
                     options,
                     options[0]);

             if( n == JOptionPane.YES_OPTION )
             {
                 newGame();
             }
             else if( n  == JOptionPane.NO_OPTION )
             {
                 System.exit( 0 );
             }
         }
    }

    /**
     * Determine how many players are available to play and which player is unavaiable
     */
    public void reportLosers()
    {
         for( int i = 0; i < players.length; i++ )
         {
             if( players[i].getChipAmount() < 0 )
             {
                   players[i].setAvailable( false );
                   current_players--;
             }
         }
    }

    /**
     * Determine who are the winners and distribute winnings to players
     */
    public void revealWinners()
    {
                String answer = questions[index].getAnswer();
                ArrayList<Integer>  list_of_winners = new ArrayList<Integer>();
                int winners = 0;
                int jackpot = Jackpot;
                int chip_amount = 0;

                for( int i = 0; i < players.length; i++ )
                {
                       if(answer.equals( players[i].getResponse() ) )
                       {
                           winners++;
                           list_of_winners.add( i );
                       }
                }

                //protects from dividing by zero
               if( winners != 0 )
               {
                     jackpot/=winners;
               }

               Jackpot = 0;
               GUI.setJackPot(Jackpot);

                for( int i = 0; i < list_of_winners.size(); i++ )
                {
                    chip_amount = players[list_of_winners.get(i)].getChipAmount();
                    updateStatus(list_of_winners.get(i),"Winner");
                    players[list_of_winners.get(i)].setChipAmount(chip_amount+jackpot);
                    GUI.getPlayerUI(list_of_winners.get(i)).updateMoneyLabel(chip_amount+jackpot);
                }
    }

    /**
     * Create Spinner and add it to a open dialog. Used to create raised value popup
     * @param optionPane the option pane to hold the spinner
     * @return the spinner
     */
    public SpinnerNumberModel getOptionPaneSpinner( final JOptionPane optionPane )
    {
        int call_price = players[active_player].getCallPrice();
        int total_amount = players[active_player].getChipAmount();
        SpinnerNumberModel model = new SpinnerNumberModel( call_price + 1, call_price + 1 , total_amount ,1 );

        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                SpinnerNumberModel updateModel = ( SpinnerNumberModel ) e.getSource();
                    optionPane.setInputValue( updateModel.getValue() );
            }
        });

         return model;
    }

    /**
     * Create a group of radio buttons to provide a response for each user.
     * @param optionPane the option pane to hold the group of buttons
     * @return the spinner
     */
    public JRadioButton[] getOptionPaneRadioGroup( final JOptionPane optionPane )
    {
        JRadioButton A = new JRadioButton( "A" );
        A.setMnemonic( KeyEvent.VK_A );
        A.setActionCommand( "A" );

        JRadioButton B = new JRadioButton( "B" );
        B.setMnemonic( KeyEvent.VK_B );
        B.setActionCommand( "B" );

        JRadioButton C = new JRadioButton( "C" );
        C.setMnemonic( KeyEvent.VK_C );
        C.setActionCommand( "C" );

        JRadioButton D = new JRadioButton( "D" );
        D.setMnemonic( KeyEvent.VK_D );
        D.setActionCommand( "D" );

        ButtonGroup group = new ButtonGroup();
        group.add( A );
        group.add( B );
        group.add( C );
        group.add( D );

        JRadioButton[] buttons = { A, B, C, D };


        A.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                optionPane.setInputValue( e.getActionCommand() );
            }
        });

       B.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed( ActionEvent e ) {
               optionPane.setInputValue( e.getActionCommand() );
           }
       });

        C.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                optionPane.setInputValue( e.getActionCommand() );
            }
        });

        D.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                optionPane.setInputValue( e.getActionCommand() );
            }
        });
             return buttons;

    }

    /**
     * Set the call price for each player. If a player is small blind for that round apply small blind rather than
     * call price. This process is the same for big bling.
     * @param price the call price
     */
    public void setCallPrice( int price )
    {
        for( int i = 0; i < 4; i++ )
        {
            if( !players[i].getBigBlind() && !players[i].getSmallBlind() )
            {
                players[i].setCallPrice( price );
            }
            else if( players[i].getBigBlind() )
            {
                players[i].setCallPrice( BigBlind );
            }
            else if( players[i].getSmallBlind() )
            {
                players[i].setCallPrice( SmallBlind );
            }
        }
    }

    /**
     * Add radio buttons to dialog  for response dialog
     * @param frame the Game frame
     */
    public void openResponseDialog(final GameFrame frame)
    {
               JOptionPane optionPane = new JOptionPane();
                JRadioButton[] buttons  = getOptionPaneRadioGroup( optionPane );
                JPanel pane = new JPanel();

                for( int i = 0; i < buttons.length; i++ )
                {
                    pane.add(buttons[i]);
                }

               optionPane.setMessage( pane );
               optionPane.setOptionType( JOptionPane.OK_CANCEL_OPTION );
               JDialog dialog = optionPane.createDialog( frame, "Response" );
               dialog.setVisible( true );

               players[active_player].setResponse( optionPane.getInputValue().toString() );

    }

    /** Open raise popwindow. Uses spinner to get raised value from player
     *
     * @param frame  the Game frame
     */
    public void openRaiseDialog( final GameFrame frame )
    {
        JOptionPane optionPane = new JOptionPane();
        int call = players[active_player].getCallPrice();
        int chip_amount = players[active_player].getChipAmount();
        int raised_price = 0;
        int selection = 0;


        model = getOptionPaneSpinner( optionPane );
        JSpinner spi = new JSpinner( model );
        selection = JOptionPane.showOptionDialog( frame,spi,"Raise",JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                new String[] {"Confirm", "Cancel"}, "No");

        updateStatus( active_player,"Raise" );

        if( selection == JOptionPane.YES_OPTION )
        {
            if(!optionPane.getInputValue().toString().equals( "uninitializedValue" ) )
            {

                System.out.println( optionPane.getInputValue() );
                raised_price = Integer.parseInt( optionPane.getInputValue().toString() );
                setCallPrice( raised_price );
                players[active_player].setChipAmount( chip_amount - raised_price );
                updateMoney( active_player, chip_amount - raised_price );
                Jackpot+=raised_price;
                GUI.setJackPot( Jackpot );

            }
            else
            {
                System.out.println( optionPane.getInputValue() );
                raised_price = call+1;
                setCallPrice( call + 1 );
                players[active_player].setChipAmount( chip_amount - raised_price );
                updateMoney( active_player, chip_amount - raised_price );
                Jackpot+=call+1;
                GUI.setJackPot( Jackpot );
            }

            endTurn( frame );
        }

    }

    /**
     * Update the GUI to show which player is active. For instance, if it is player one's turn then
     * the GUI will be colored blue while the others are grayed out.
     * @param player active player
     * @param frame the Game frame
     */
    public void updateMiniGUI( final int player, final GameFrame frame )
    {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                      for( int i = 0; i < 4; i++ )
                      {
                          if(i != player )
                          {
                              try {
                                  GUI.getPlayerUI( i ).setImage( "/imgs/guigray.png" );
                              } catch (IOException e) {
                                  e.printStackTrace();
                              }

                          }
                          else
                          {
                              try {
                                  GUI.getPlayerUI( i ).setImage( "/imgs/guiblue.png" );
                              } catch ( IOException e ) {
                                  e.printStackTrace();
                              }
                          }
                      }
                        window.repaint();

            }
        });
    }

    /**
     * Update the status for each GUI. For instance, "Winner", "Call", "Raise", etc.
     * @param playerIndex index for each player. Player 1 is index 0
     * @param text the status for the player
     */
    public void updateStatus( final int playerIndex, final String text )
    {

        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                if( playerIndex == -1 )
                {
                    for( int i = 0; i < players.length; i++ )
                    {
                        GUI.getPlayerUI( i ).updateStatusLabel( text );
                    }
                }
                else
                {
                    GUI.getPlayerUI( playerIndex ).updateStatusLabel( text );
                }

            }
        });

    }

    /**
     *  Update money label for a player.
     * @param playerIndex index for each player. Player 1 is index 0.
     * @param value the money value for the player.
     */
    public void updateMoney( int playerIndex, int value )
    {
        GUI.getPlayerUI( playerIndex ).updateMoneyLabel( value );
    }

    /**
     * Reveal each part of the question per round. Part one will reveal the subject. Part two will reveal
     * the difficult. Part three will reveal the whole question.
     * @param frame the Game frame
     */
    public void revealQuestionParts( final GameFrame frame )
    {

                     SwingUtilities.invokeLater( new Runnable() {
                         @Override
                         public void run() {

                             if ( !GUI.getInitialState() ) {

                                  switch( turns )
                                 {
                                     case 0:
                                     window.add( GUI.getQuestionCard( index ).getSubjectCard() );
                                     break;
                                     case 1:
                                     window.add( GUI.getQuestionCard( index ).getDifficultCard() );
                                     break;
                                     case 2:
                                     window.add( GUI.getQuestionCard( index ).getQuestionCard() );
                                     GUI.setInitialState( true );
                                     break;
                                 }
                                 frame.validate();
                                 fadeQuestionIn();
                             } else {
                                  fadeQuestionOut();
                                   window.remove( GUI.getQuestionCard( index ).getQuestionCard() );
                                   window.remove( GUI.getQuestionCard( index ).getSubjectCard() );
                                   window.remove( GUI.getQuestionCard( index ).getDifficultCard() );
                                   index++;
                                   GUI.setInitialState( false );
                                   window.add( GUI.getQuestionCard( index ).getSubjectCard() );

                                    frame.validate();
                                    fadeQuestionIn();
                             }
                         }
                     });
    }

    /**
     * Fade Question animations. Slowly decreases the alpha property to fade question out
     *
     */
     public void fadeQuestionOut()
     {
         fadeOut = new Timer( 100, new ActionListener()
         {
             float direction = -0.05f;
             public void actionPerformed( ActionEvent evt )
             {
                 isRunning_fadeOut = true;
                 float alpha =  GUI.getQuestionCard( index ).getQuestionCard().getAlpha();
                 alpha+=direction;

                 if(alpha < 0)
                 {
                     alpha = 0;
                 }

                 GUI.getQuestionCard( index ).getQuestionCard().setAlpha( alpha );
                 GUI.getQuestionCard( index ).getDifficultCard().setAlpha( alpha );
                 GUI.getQuestionCard( index ).getSubjectCard().setAlpha( alpha );

                   if( alpha == 0 )
                   {
                        isRunning_fadeOut = false;
                        fadeOut.stop();
                   }
                    else
                   {
                       fadeOut.stop();
                       fadeOut.restart();
                   }
             }
         } );
                     fadeOut.start();

     }

    /**
     * Fade Questions Animation. Slowly increase the alpha property to fade question in.
     */
    public void fadeQuestionIn()
    {

        fadeIn = new Timer( 100, new ActionListener()
        {
            float direction = 0.05f;
            public void actionPerformed( ActionEvent evt )
            {
                float alpha = 0.0f;
                isRunning_fadeIn = true;
                switch( turns )
                {
                    case 0:
                    alpha = GUI.getQuestionCard( index ).getSubjectCard().getAlpha();
                    break;
                    case 1:
                    alpha = GUI.getQuestionCard( index ).getDifficultCard().getAlpha();
                    break;
                    case 2:
                    alpha = GUI.getQuestionCard( index ).getQuestionCard().getAlpha();
                    break;
                }
                alpha+=direction;

                alpha = (alpha > 1) ? 1 : alpha;
                switch( turns )
                {
                    case 0:
                    GUI.getQuestionCard( index ).getSubjectCard().setAlpha( alpha );
                    break;
                    case 1:
                    GUI.getQuestionCard( index ).getDifficultCard().setAlpha( alpha );
                    break;
                    case 2:
                    GUI.getQuestionCard( index ).getQuestionCard().setAlpha( alpha );
                    break;
                }

                if( alpha == 1 )
                {
                    isRunning_fadeIn = false;
                    fadeIn.stop();
                }
                else
                {
                    fadeIn.stop();
                    fadeIn.restart();
                }
            }
        } );
                    fadeIn.start();
    }

    /**
     * Create and show game board
     * @param frame  the Game frame
     */
    public void createAndShowGameBoard( final GameFrame frame )
    {
                   try {
                       board = ImageIO.read( getClass().getResourceAsStream( "/imgs/board.png" ) );

                   } catch ( IOException e ) {
                       e.printStackTrace();
                   }

                   SwingUtilities.invokeLater( new Runnable() {
                       @Override
                       public void run() {
                           window.setImage( board );
                           window.setLayout( null );
                           frame.setContentPane( window );
                           try {
                               GUI.createGUI( frame, players, questions );
                           } catch ( IOException e ) {
                               e.printStackTrace();
                           }
                           frame.validate();
                       }
                   });
    }

    /**
     * Read into questions from configuration file and store for later use
     */
    public void initializeQBank()
    {
        Scanner sc;
         int x = 0;

        String currentDir = new File("").getAbsolutePath();

            try {
                file =  new File( "conf/questionbank.txt" );
                sc = new Scanner( file );
                num_of_q = sc.nextInt();
                questions = new QuestionSet[num_of_q];
                sc.nextLine();
                while ( x < num_of_q ) {
                    questions[x] = new QuestionSet( sc.nextLine(), sc.nextLine(), sc.nextLine(), sc.nextLine() );
                    x++;
                }
                sc.close();

                //Shuffle questions
                Collections.shuffle( Arrays.asList( questions ) );
            } catch ( FileNotFoundException e ) {
                e.printStackTrace();
            }
    }

    /**
     * Heads Up Display for Game. Creates miniUI, text labels, and other GUI components.
     */
 public class HUD {

     private MiniUI player1UI;
     private MiniUI player2UI;
     private MiniUI player3UI;
     private MiniUI player4UI;
     private int jackpot;
     private QuestionCard[] questionCards;
     private JLabel JackPot_Label;
     private boolean initialState;

     public class MiniUI extends JPanel {
             private BufferedImage image;
             private int player;
             private JLabel MoneyLabel;
             private JLabel StatusLabel;

            // Create miniUI for each player. Contains a text label for current amount of money and the current
            // status for player. For instance, the status can be called "Call" or "Winner"
            MiniUI( String src, int player ) throws  IOException
            {
                image = ImageIO.read( getClass().getResourceAsStream( src ) );
                this.player = player;
                this.setLayout( null );

                setPreferredSize( new Dimension( 125, 168 ));

                    MoneyLabel = new JLabel( "$$$", JLabel.LEADING );
                    StatusLabel = new JLabel( "???", JLabel.LEADING );
                    MoneyLabel.setBounds( 50, 5, 50, 300 );
                    StatusLabel.setBounds( 40,5,50,10 );
                    this.add( MoneyLabel );
                    this.add( StatusLabel );
                    repaint();
            }

         /** Update Money label for player. Include the $ sign before the money value
          *
          * @param value the money value
          */
            public void updateMoneyLabel( int value )
            {
                MoneyLabel.setText( "$" + Integer.toString( value ) );
            }

         /**
          * Update the Status label for player.
          * @param status the status
          */
            public void updateStatusLabel( String status )
            {
                StatusLabel.setText( status );
            }

         /**
          * Set the image of the miniUI
          * @param src the location of the image
          * @throws IOException
          */
            public void setImage( String src ) throws IOException {
                this.image = null;
                this.image = ImageIO.read( getClass().getResourceAsStream( src ) );
                this.repaint();
            }

         /**
          * Overrides the paintComponent so the JPanel appears as an image.
          * @param g  the graphic of the JPanel
          */
            @Override
            public void paintComponent( Graphics g ) {
                super.paintComponents( g );
                g.drawImage( image,0,0,null );
        }
 }

    /**
     *  A class that deals with maniuplation and creation of a question card
     */
     public class QuestionCard {
            private BufferedImage image;
            private int player;
            private LabelFade question;
            private LabelFade subject;
            private LabelFade difficulty;

        /**
         * Create a question card. Each contains its individual question, subject, and difficulty
         * @param question the question string
         * @param subject the subject string
         * @param difficulty the difficult string
         */
            QuestionCard(String question, String subject, String difficulty){
                setPreferredSize(new Dimension( 125, 168));

                this.question = new LabelFade("<html>Question: " + question + "</html>", JLabel.LEADING);
                this.subject = new LabelFade("Subject: " + subject, JLabel.LEADING);
                this.difficulty = new LabelFade("Difficulty: " + difficulty, JLabel.LEADING);

                this.question.setFont(new Font("Monospaced", Font.BOLD, 14));
                this.question.setForeground(Color.WHITE);
                this.subject.setFont(new Font("Monospaced", Font.BOLD, 14));
                this.subject.setForeground(Color.WHITE);
                this.difficulty.setFont(new Font("Monospaced", Font.BOLD, 14));
                this.difficulty.setForeground(Color.WHITE);


                this.question.setBounds(200,180,600,300);
                this.subject.setBounds(200,100,150,100);
                this.difficulty.setBounds(680,100,150,100);
                initialState = false;
            }

         public LabelFade getQuestionCard()
         {
                return this.question;
         }

         public LabelFade getSubjectCard()
         {
             return this.subject;
         }

         public LabelFade getDifficultCard()
         {
             return this.difficulty;
         }
     }

     //A Jlabel meant to fade in and fade out of screen
     public class LabelFade extends JLabel
     {
         private float alpha;

         public LabelFade(String text, int alignment)
         {
             setText(text);
             setHorizontalAlignment(alignment);
             setAlpha(0f);
         }

         public void setAlpha(float value)
         {
             if (alpha != value) {
                 float old = alpha;
                 alpha = value;
                 firePropertyChange("alpha", old, alpha);
                 repaint();
             }

         }

         public float getAlpha()
         {
             return alpha;
         }

         @Override
         public void paint(Graphics g)
         {
             Graphics2D g2d = (Graphics2D) g.create();
             g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));
             super.paint(g2d);
             g2d.dispose();

         }

     }

        /**
         * Initialize and create the GUI for the game
         * @param frame the Game frame
         * @param players the players
         * @param questions the questions
         * @throws IOException
         */
     public void createGUI(GameFrame frame, Players[] players, QuestionSet[] questions) throws IOException {
            Random rand = new Random();

            player1UI = new MiniUI("/imgs/guiblue.png",1);
            player1UI.setBounds(45, 230, 127, 168);
            player1UI.updateMoneyLabel(players[0].getChipAmount());
            player1UI.setImage("/imgs/guiblue.png");

            player2UI = new MiniUI("/imgs/guiblue.png",2);
            player2UI.setBounds(450,30,127,168);
            player2UI.updateMoneyLabel(players[1].getChipAmount());


            player3UI = new MiniUI("/imgs/guiblue.png",4);
            player3UI.setBounds(870,230,127,168);
            player3UI.updateMoneyLabel(players[3].getChipAmount());

            player4UI = new MiniUI("/imgs/guiblue.png",3);
            player4UI.setBounds(450,500,127,168);
            player4UI.updateMoneyLabel(players[2].getChipAmount());


            questionCards = new QuestionCard[questions.length];
            for(int i = 0; i < questions.length; i++)
            {
                    questionCards[i] = new QuestionCard(questions[i].getQuestion(),questions[i].getSubject(), questions[i].getDifficulty());
            }

            window.add(player1UI);
            window.add(player2UI);
            window.add(player3UI);
            window.add(player4UI);

            bCheck.setText("Check");
            bResponse.setText("Response");
            bRaise.setText("Raise");

            bCheck.setBounds(100,700,100,40);
            bCall.setBounds(300,700,100,40);
            bRaise.setBounds(500,700,100,40);
            bResponse.setBounds(700,700,100,40);

            JackPot_Label = new JLabel("$$ Jackpot: ");
            JackPot_Label.setFont(new Font("Monospaced", Font.BOLD, 18));
            JackPot_Label.setForeground(Color.WHITE);


            JackPot_Label.setBounds(200,430,200,100);

            window.add(JackPot_Label);

            window.add(bCheck);
            window.add(bCall);
            window.add(bRaise);
            window.add(bResponse);
            frame.validate();
        }
     // Set up jackpot label
     public void setJackPot(int value)
     {
               jackpot = value;
              JackPot_Label.setText("$$ Jackpot: " + Integer.toString(value));
     }
     // Retrieve miniUI for player
     public MiniUI getPlayerUI(int playerIndex)
     {
         MiniUI handler  = null;
             if(playerIndex == 0)
             {
                 handler = player1UI;
             }
             else if(playerIndex == 1)
             {
                 handler = player2UI;
             }
             else if(playerIndex == 2)
             {
                 handler =  player3UI;
             }
             else if(playerIndex == 3)
             {
                 handler = player4UI;
             }

             return handler;

     }

     public QuestionCard getQuestionCard(int index)
     {
         return questionCards[index];
     }

     public void setInitialState(boolean state)
     {
           initialState = state;
     }

     public boolean getInitialState()
     {
         return initialState;
     }
}
}

//The main window for game.
class MainWindow extends JPanel
{
    private final int WIDTH;
    private final int HEIGHT;
    private BufferedImage image;

    public MainWindow(int w, int h)
    {
             setSize(new Dimension( w, h));
             WIDTH = w;
             HEIGHT = h;
    }

    public void setImage(BufferedImage src)
    {
        this.image = src;
        repaint();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);

    }

}
