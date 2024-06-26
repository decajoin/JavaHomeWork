package niss.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.security.auth.callback.ConfirmationCallback;

public class PlayPork {
	public ArrayList<Card> nowCards;
    public Player[] players;
    public boolean[] propriety;

    // PlayPork构造函数，进行牌局的初始化
    public PlayPork() {
        // 初始化 players 数组
        players = new Player[4];
        players[0] = new Player(1, "小红");
        players[1] = new Player(2, "小白");
        players[2] = new Player(3, "小绿");
        players[3] = new Player(4, "小青");

        // 初始化 propriety 数组
        propriety = new boolean[4];
        int firstPlayerIndex = new Random().nextInt(4);
        propriety[firstPlayerIndex] = true;

        // 初始化牌
        nowCards = new ArrayList<>();
        for (int suit = 1; suit <= 4; suit++) {
            for (int number = 1; number <= 13; number++) {
                nowCards.add(new Card(number, suit));
            }
        }

        // 洗牌
        shuffle();

        // 发牌给每个玩家
        dealCards();

        // 每个玩家进行理牌，按花色大小进行理牌
        for (int i = 0; i < 4; i++) {
        	players[i].organizeCardsBySuit();
        }
    }

    // 打印玩家出牌优先级顺序
    public void printPropriety() {
    	for (int i = 0; i < 4; i++) {
    		if (propriety[i]) System.out.println("获得首次出牌权的是 " + players[i]);
    	}
    }

    // 更新出牌优先权
    public void updatePropriety(int currentPlayerIndex) {
        // 将上一轮出牌的玩家的出牌优先权设为 false
        propriety[currentPlayerIndex] = false;
        
        // 找到下一轮应该出牌的玩家的索引
        int nextPlayerIndex = (currentPlayerIndex + 1) % 4;

        // 更新下一轮出牌的玩家的出牌优先权
        propriety[nextPlayerIndex] = true;
    }
    
    // 洗牌
    public void shuffle() {
        Collections.shuffle(nowCards);
    }

    // 发牌给每个玩家
    private void dealCards() {
        int cardIndex = 0;
        for (int i = 0; i < 4; i++) {
            ArrayList<Card> playerCards = new ArrayList<>();
            for (int j = 0; j < 13; j++) {
                playerCards.add(nowCards.get(cardIndex));
                cardIndex++;
            }
            players[i].setCards(playerCards);
        }
    }
    
    // 判断这一局是否结束，只要有一个玩家手牌为0就结束
    public boolean isOver() {
    	for (int i = 0; i < 4; i++) {
    		if (players[i].isTheCardCleared()) return true;
    	}
    	
    	return false;
    }
    
    // 计算每个玩家这回合的得分
    public void calculate() {
    	ArrayList<Integer> countPlayerCards = new ArrayList<Integer>();
    	for (int i = 0; i < 4; i++) {
    		countPlayerCards.add(players[i].countCards());
    	}
    	Collections.sort(countPlayerCards);
    	for (int i = 0; i < 4; i++) {
    		for (int j = 0; j < 4; j++) {
    			if (players[j].countCards() == countPlayerCards.get(i)) {
    				if (i == 0) {
    					players[j].setTotalScore(5);
    				}
    				else if (i == 1) {
    					players[j].setTotalScore(3);
    				}
    				else if (i == 2) {
    					players[j].setTotalScore(2);
    				}
    				else players[j].setTotalScore(1);
    			}
    		}
    	}
    	
    }
    
    // 由优先级数组得到现在该出牌的玩家号码
    public int getPlayerIndex(boolean []propriety) {
    	for (int i = 0; i < 4; i++) {
    		if (propriety[i]) return i;
    	}
		return 0;
    }
     
    // 打印本轮对局信息
    public void printPlayPorkInfo() {
    	for (int i = 0; i < 4; i++) {
    		System.out.println(players[i]);
    	}
    }
    
    
    // 开始玩一局
    public void playAGame() {
    	printPropriety();
    	Card lastcCard = new Card();
    	
    	int currentPlayerIndex = getPlayerIndex(propriety);
    	
    	// 记录上一个出牌的人的编号
    	int lastPlayerIndex = currentPlayerIndex;
    	
    	Player currentPlayer = players[currentPlayerIndex];
    	// 第一个出牌，出牌方式1
    	lastcCard = currentPlayer.playingCard1();
    	currentPlayer.displayCards();
    	updatePropriety(currentPlayerIndex);
    	
    	while (!isOver()) {
    		currentPlayerIndex = getPlayerIndex(propriety);
    		currentPlayer = players[currentPlayerIndex];
    		
    		Card tempCard = currentPlayer.playingCard2(lastcCard, lastPlayerIndex);
    		// 只有返回的牌不为空，也就是说，有玩家出牌才更新
    		if (tempCard != null) {
    			lastcCard = tempCard;
    			lastPlayerIndex = currentPlayerIndex;
    		}
    		
    		currentPlayer.displayCards();
    		updatePropriety(currentPlayerIndex);
    	}
    	
    	System.out.println("本轮游戏结束");
    	
    	calculate();
    	
    	printPlayPorkInfo();
    }
}
