/**
 * Represents traditional deck of 52 playing cards.
 */
import java.util.Random;
public class Deck {

        /**
         * Deck is represented by an array of 52 cards.
         */
        private Card[] cards = new Card[52];

        /**
         * Initializes card values and then shuffles the deck.
         */
        public Deck() {
                for (int i = 0; i < cards.length; i++) {
                        if (i < 13) {
                                cards[i] = new Card("hearts", 13 - i);
                        }
                        else if (i < 26)
                                cards[i] = new Card("diamonds", 26 - i);
                        else if (i < 39) 
                                cards[i] = new Card("spades", 39 - i);
                        else if (i < 52)
                                cards[i] = new Card("clubs", 52 - i);
                }
                shuffle();
        }

        /**
         * Fisher-Yates shuffle used to shuffle elements of card array
         * in place.
         */
        private void shuffle() {
                Random numGen = new Random();
                for (int i = cards.length - 1; i > 0; i--) {
                        int j = numGen.nextInt(i);
                        Card tmp = cards[i];
                        cards[i] = cards[j];
                        cards[j] = tmp;
                }
        }

        /**
         * Gets the card at the given index.
         * @param index The index of the card.
         * @return The card at index.
         */
        public Card getCard(int index) {
                return cards[index];
        }

        @Override
        public String toString() {
                String deckAsString = "";
                for (int i = 0; i < cards.length; i++) {
                        deckAsString += cards[i].toString();
                        deckAsString += "\n";
                }
                return deckAsString;
        }
}
