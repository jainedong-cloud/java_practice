package game;

/*
We are building the back-end for an online gaming platform. The system tracks players and their match history. Each match result records the outcome from that player's perspective.

Definitions:
* A "Player" has an ID and a username.
* A "MatchResult" records one player's result in a match: player ID, opponent ID, outcome (WIN, LOSS, or DRAW), score, and timestamp.
* A "GameManager" manages players and match results and provides player statistics.

To begin with, two tasks:
1) Read through and understand the code below. Take as much time as you need, and feel free to run the code.
2) The test for GameManager is not passing due to a bug in the code. Make the necessary changes to GameManager to fix the bug.
*/

// ===============================================================================
/*
Now that the platform can track player statistics, we want to record match results and compute per-player score statistics. Each MatchResult represents one player's experience of a single match: playerId is the player the record belongs to, opponentId is their opponent, and score is that player's score.

2-1) Add addMatchResult() to GameManager. This function takes a MatchResult and stores it. Only playerId is validated: if it does not refer to a known player in GameManager, the match result should be ignored. A match result with a known playerId must still be stored even when opponentId does not refer to a known player.

2-2) Add getAverageScoreByOutcome() to GameManager. This function takes a player ID and returns a map mapping each outcome (WIN, LOSS, DRAW) to the player's average score for that outcome. Only outcomes the player has at least one result for should appear in the map. Return an empty map if the player has no match results.

The testAddMatchResult and testGetAverageScoreByOutcome tests are provided.
*/

// ===============================================================================
/*
Now that the platform can record match results, we want to summarize the match history between two specific players. For each pair of players, we want to know how each player has performed against the other and when they last played. Each match is recorded twice, once from each player's perspective, using two MatchResult records with the same timestamp.

3) Add getHeadToHead() to GameManager. This function takes two player IDs and returns a HeadToHead object summarizing all matches between them.

   The HeadToHead class has been added to represent this summary:
     - winsPlayer1: number of matches the first player won
     - winsPlayer2: number of matches the second player won
     - draws: number of matches that ended in a draw
     - totalMatches: total number of matches played between them
     - lastResult: the outcome of the most recent match from the first player's perspective (null if they have never played)
     - lastMatchTimestamp: timestamp of the most recent match between them (null if they have never played)

   If the two players have never faced each other, return a HeadToHead with all numeric fields set to 0, lastResult set to null, and lastMatchTimestamp set to null.

The testGetHeadToHead test is provided.
*/
import java.util.*;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import static org.junit.Assert.*;

enum Outcome {
    WIN, LOSS, DRAW
}

class Player {
    public int playerId;
    public String username;

    public Player(int playerId, String username) {
        this.playerId = playerId;
        this.username = username;
    }
}

class MatchResult {
    public int playerId;
    public int opponentId;
    public Outcome outcome;
    public int score;
    public int timestamp;

    public MatchResult(int playerId, int opponentId, Outcome outcome, int score, int timestamp) {
        this.playerId = playerId;
        this.opponentId = opponentId;
        this.outcome = outcome;
        this.score = score;
        this.timestamp = timestamp;
    }
}

class PlayerStats {
    public int totalMatches;
    public int wins;
    public double winRate;

    public PlayerStats(int totalMatches, int wins, double winRate) {
        this.totalMatches = totalMatches;
        this.wins = wins;
        this.winRate = winRate;
    }
}

class HeadToHead {
    public int winsPlayer1;
    public int winsPlayer2;
    public int draws;
    public int totalMatches;
    public Outcome lastResult;
    public Integer lastMatchTimestamp;

    public HeadToHead(int winsPlayer1, int winsPlayer2, int draws,
                      int totalMatches, Outcome lastResult, Integer lastMatchTimestamp) {
        this.winsPlayer1 = winsPlayer1;
        this.winsPlayer2 = winsPlayer2;
        this.draws = draws;
        this.totalMatches = totalMatches;
        this.lastResult = lastResult;
        this.lastMatchTimestamp = lastMatchTimestamp;
    }
}

class GameManager {
    public Map<Integer, Player> players = new HashMap<>();
    public List<MatchResult> matchResults = new ArrayList<>();

    public void addPlayer(Player player) {
        players.put(player.playerId, player);
    }

    public PlayerStats getPlayerStatistics(int playerId) {
        /*
            Return statistics for a single player:
            * totalMatches: total number of matches this player has played (wins, losses, and draws)
            * wins:         number of those matches the player won
            * winRate:      wins / totalMatches, expressed as a value between 0.0 and 1.0
        */
        List<MatchResult> playerMatches = new ArrayList<>();
        for (MatchResult m : matchResults) {
            if (m.playerId == playerId) {
                playerMatches.add(m);
            }
        }

        int totalMatches = 0;
        for (MatchResult m : playerMatches) {
            if (m.outcome == Outcome.WIN || m.outcome == Outcome.LOSS) {
                totalMatches++;
            }
        }

        int wins = 0;
        for (MatchResult m : playerMatches) {
            if (m.outcome == Outcome.WIN) {
                wins++;
            }
        }

        double winRate;
        if (totalMatches > 0) {
            winRate = (double) wins / totalMatches;
        } else {
            winRate = 0.0;
        }

        return new PlayerStats(totalMatches, wins, winRate);
    }
}

public class Solution {
    public static class TestSuite {
        @Test
        public void testGetPlayerStatistics() {
            GameManager gm = new GameManager();
            gm.addPlayer(new Player(1, "player1"));
            gm.addPlayer(new Player(2, "player2"));

            gm.matchResults.add(new MatchResult(1, 2, Outcome.WIN, 80, 1000));
            gm.matchResults.add(new MatchResult(1, 2, Outcome.LOSS, 50, 2000));
            gm.matchResults.add(new MatchResult(1, 2, Outcome.DRAW, 60, 3000));
            gm.matchResults.add(new MatchResult(1, 2, Outcome.WIN, 90, 4000));

            PlayerStats stats = gm.getPlayerStatistics(1);
            assertEquals(4, stats.totalMatches);
            assertEquals(2, stats.wins);
            assertTrue(Math.abs(stats.winRate - 0.5) < 1e-4);

            gm.matchResults.add(new MatchResult(2, 1, Outcome.DRAW, 60, 1000));
            gm.matchResults.add(new MatchResult(2, 1, Outcome.DRAW, 60, 2000));

            PlayerStats stats2 = gm.getPlayerStatistics(2);
            assertEquals(2, stats2.totalMatches);
            assertEquals(0, stats2.wins);
            assertTrue(Math.abs(stats2.winRate - 0.0) < 1e-4);
        }

        @Test
        public void testAddMatchResult() {
            GameManager gm = new GameManager();
            gm.addPlayer(new Player(1, "player1"));
            gm.addPlayer(new Player(2, "player2"));

            gm.addMatchResult(new MatchResult(1, 2, Outcome.WIN, 80, 1000));
            gm.addMatchResult(new MatchResult(2, 1, Outcome.LOSS, 50, 1000));

            // Unknown player is ignored.
            gm.addMatchResult(new MatchResult(99, 1, Outcome.WIN, 100, 2000));

            // Known player with an unregistered opponent is still stored.
            gm.addMatchResult(new MatchResult(1, 99, Outcome.WIN, 70, 3000));

            assertEquals(3, gm.matchResults.size());
            assertEquals(1, gm.matchResults.get(0).playerId);
            assertEquals(2, gm.matchResults.get(1).playerId);
            assertEquals(1, gm.matchResults.get(2).playerId);
            assertTrue(gm.matchResults.stream().anyMatch(m -> m.opponentId == 99));
        }

        @Test
        public void testGetAverageScoreByOutcome() {
            GameManager gm = new GameManager();
            gm.addPlayer(new Player(1, "player1"));
            gm.addPlayer(new Player(2, "player2"));

            // Match 1: player 1 wins.
            gm.addMatchResult(new MatchResult(1, 2, Outcome.WIN, 80, 1000));
            gm.addMatchResult(new MatchResult(2, 1, Outcome.LOSS, 50, 1000));

            // Match 2: player 1 wins again.
            gm.addMatchResult(new MatchResult(1, 2, Outcome.WIN, 91, 2000));
            gm.addMatchResult(new MatchResult(2, 1, Outcome.LOSS, 61, 2000));

            // Match 3: draw.
            gm.addMatchResult(new MatchResult(1, 2, Outcome.DRAW, 70, 3000));
            gm.addMatchResult(new MatchResult(2, 1, Outcome.DRAW, 70, 3000));

            // Match 4: another draw.
            gm.addMatchResult(new MatchResult(1, 2, Outcome.DRAW, 75, 4000));
            gm.addMatchResult(new MatchResult(2, 1, Outcome.DRAW, 75, 4000));

            // Match 5: player 1 wins a third time.
            gm.addMatchResult(new MatchResult(1, 2, Outcome.WIN, 54, 5000));
            gm.addMatchResult(new MatchResult(2, 1, Outcome.LOSS, 69, 5000));

            Map<Outcome, Double> avg1 = gm.getAverageScoreByOutcome(1);
            assertTrue(avg1.containsKey(Outcome.WIN));
            assertEquals(75.0, avg1.get(Outcome.WIN), 1e-4);   // (80+91+54)/3
            assertTrue(avg1.containsKey(Outcome.DRAW));
            assertEquals(72.5, avg1.get(Outcome.DRAW), 1e-4);  // (70+75)/2
            assertFalse(avg1.containsKey(Outcome.LOSS));       // Player 1 has no losses.

            Map<Outcome, Double> avg2 = gm.getAverageScoreByOutcome(2);
            assertTrue(avg2.containsKey(Outcome.LOSS));
            assertEquals(60.0, avg2.get(Outcome.LOSS), 1e-4);  // (50+61+69)/3
            assertTrue(avg2.containsKey(Outcome.DRAW));
            assertEquals(72.5, avg2.get(Outcome.DRAW), 1e-4);  // (70+75)/2
            assertFalse(avg2.containsKey(Outcome.WIN));        // Player 2 has no wins.

            // Outcome with exactly one result.
            gm.addPlayer(new Player(4, "player4"));
            gm.addMatchResult(new MatchResult(4, 1, Outcome.WIN, 42, 6000));
            Map<Outcome, Double> avg4 = gm.getAverageScoreByOutcome(4);
            assertTrue(avg4.containsKey(Outcome.WIN));
            assertEquals(42.0, avg4.get(Outcome.WIN), 1e-4);
            assertFalse(avg4.containsKey(Outcome.LOSS));
            assertFalse(avg4.containsKey(Outcome.DRAW));

            // Player with no match results.
            gm.addPlayer(new Player(3, "player3"));
            assertTrue(gm.getAverageScoreByOutcome(3).isEmpty());
        }

        @Test
        public void testGetHeadToHead() {
            GameManager gm = new GameManager();
            gm.addPlayer(new Player(1, "player1"));
            gm.addPlayer(new Player(2, "player2"));
            gm.addPlayer(new Player(3, "player3"));
            gm.addPlayer(new Player(4, "player4"));

            // Match 1: player 1 wins.
            gm.addMatchResult(new MatchResult(1, 2, Outcome.WIN, 80, 1000));
            gm.addMatchResult(new MatchResult(2, 1, Outcome.LOSS, 50, 1000));

            // Match 4: player 1 wins (most recent).
            gm.addMatchResult(new MatchResult(1, 2, Outcome.WIN, 85, 4000));
            gm.addMatchResult(new MatchResult(2, 1, Outcome.LOSS, 65, 4000));

            // Match 5: player 3 beats player 2. Not part of the player 1 vs 2 history.
            gm.addMatchResult(new MatchResult(3, 2, Outcome.WIN, 75, 5000));
            gm.addMatchResult(new MatchResult(2, 3, Outcome.LOSS, 55, 5000));

            // Match 2: player 2 wins.
            gm.addMatchResult(new MatchResult(1, 2, Outcome.LOSS, 60, 2000));
            gm.addMatchResult(new MatchResult(2, 1, Outcome.WIN, 90, 2000));

            // Match 6: player 3 beats player 1. Single match, older than every player 1 vs 2 match.
            gm.addMatchResult(new MatchResult(1, 3, Outcome.LOSS, 60, 500));
            gm.addMatchResult(new MatchResult(3, 1, Outcome.WIN, 70, 500));

            // Match 3: draw.
            gm.addMatchResult(new MatchResult(1, 2, Outcome.DRAW, 70, 3000));
            gm.addMatchResult(new MatchResult(2, 1, Outcome.DRAW, 70, 3000));

            HeadToHead h2h = gm.getHeadToHead(1, 2);
            assertEquals(2, h2h.winsPlayer1);               // Matches 1 and 4.
            assertEquals(1, h2h.winsPlayer2);               // Match 2.
            assertEquals(1, h2h.draws);                     // Match 3.
            assertEquals(4, h2h.totalMatches);
            assertEquals(Outcome.WIN, h2h.lastResult);      // Match 4 was a win for player 1.
            assertNotNull(h2h.lastMatchTimestamp);
            assertEquals(4000, h2h.lastMatchTimestamp.intValue());

            // From player 2's perspective.
            HeadToHead h2hReverse = gm.getHeadToHead(2, 1);
            assertEquals(1, h2hReverse.winsPlayer1);        // Player 2 won match 2.
            assertEquals(2, h2hReverse.winsPlayer2);        // Player 1 won matches 1 and 4.
            assertEquals(1, h2hReverse.draws);
            assertEquals(4, h2hReverse.totalMatches);
            assertEquals(Outcome.LOSS, h2hReverse.lastResult);  // Match 4 was a loss for player 2.
            assertNotNull(h2hReverse.lastMatchTimestamp);
            assertEquals(4000, h2hReverse.lastMatchTimestamp.intValue());

            // Single-match pair: player 1 vs player 3 (Match 6 only).
            HeadToHead h2hSingle = gm.getHeadToHead(1, 3);
            assertEquals(0, h2hSingle.winsPlayer1);
            assertEquals(1, h2hSingle.winsPlayer2);
            assertEquals(0, h2hSingle.draws);
            assertEquals(1, h2hSingle.totalMatches);
            assertEquals(Outcome.LOSS, h2hSingle.lastResult);
            assertNotNull(h2hSingle.lastMatchTimestamp);
            assertEquals(500, h2hSingle.lastMatchTimestamp.intValue());

            // Single-match pair: player 3 vs player 2 (Match 5 only).
            HeadToHead h2hOther = gm.getHeadToHead(3, 2);
            assertEquals(1, h2hOther.winsPlayer1);
            assertEquals(0, h2hOther.winsPlayer2);
            assertEquals(0, h2hOther.draws);
            assertEquals(1, h2hOther.totalMatches);
            assertEquals(Outcome.WIN, h2hOther.lastResult);
            assertNotNull(h2hOther.lastMatchTimestamp);
            assertEquals(5000, h2hOther.lastMatchTimestamp.intValue());

            // Players who have never faced each other.
            HeadToHead h2hEmpty = gm.getHeadToHead(1, 4);
            assertEquals(0, h2hEmpty.totalMatches);
            assertEquals(0, h2hEmpty.winsPlayer1);
            assertEquals(0, h2hEmpty.winsPlayer2);
            assertEquals(0, h2hEmpty.draws);
            assertNull(h2hEmpty.lastResult);
            assertNull(h2hEmpty.lastMatchTimestamp);
        }

    }

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestSuite.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.getTrace());
        }
        if (result.wasSuccessful()) {
            System.out.println("All tests passed successfully.");
        } else {
            System.err.println("Tests failed: " + result.getFailureCount() + " test(s) failed.");
            System.exit(1);
        }
    }
}