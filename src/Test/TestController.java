/* Quinn Keenan, 301504914, 19/09/2025
I plan on abstracting the complexities present in the main method by implementing event handling throughout
my codebase. For the time being, this is perfectly functional and well beyond the scope of a simple school lab
assignment. However, I'm in an academic program called "Software Engineering Technology" and I'd like to live
up to the title rather than doing trivial, mindless tasks for easy, high grades. Furthermore, I considered
creating an abstraction to combine the logic for the two input-parsing methods, but they are too different
to do so without it being a huge headache. The same applies to the JOptionPane method calls in the entry point.
I may reconsider in the future. */

package Test;

import TestQuestion.TestQuestionBuilder;
import javax.swing.JOptionPane;

public class TestController
{
    public static void main(String[] args)
    {
        boolean answerIsCorrect;
		String answerMessage;
		String correctOptionString;
		Test test = Test.createTest(promptForNumberOfQuestionsAndParse());

		if (!test.isNewlyCreated())
		{
			JOptionPane.showMessageDialog(
				null,
				"An active instance of Test already exists and therefore a new instance was not instantiated.",
				"No new test instantiated!",
				JOptionPane.WARNING_MESSAGE
			);
		}

		while (test.isActive())
		{
			correctOptionString = test.getCurrentQuestionCorrectOptionString();
			answerIsCorrect = test.incrementScoreAfterAnswerAndAdvanceToNextQuestion(displayCurrentQuestionAndParseAnswer(test));
			answerMessage = test.getRandomAnswerMessage(answerIsCorrect);

			if (answerIsCorrect)
			{
				JOptionPane.showMessageDialog(
					null,
					answerMessage,
					"Correct answer!",
					JOptionPane.INFORMATION_MESSAGE
				);
			}
			else
			{
				JOptionPane.showMessageDialog(
					null,
					String.format("%s The correct answer was: \"%s.\"", answerMessage, correctOptionString),
					"Incorrect answer!",
					JOptionPane.INFORMATION_MESSAGE
				);
			}
		}

		JOptionPane.showMessageDialog(
			null,
			test.getScoreString(),
			"Test completed!",
			JOptionPane.INFORMATION_MESSAGE
		);
    }

	private static byte displayCurrentQuestionAndParseAnswer(Test test)
	{
		String userInput;
		byte userInputAsByte;

		while (true)
		{
			userInput = JOptionPane.showInputDialog(
				null,
				test.getCurrentQuestionString()
			);

			try
			{
				if (userInput == null)
				{
					throw new InvalidStringInputException("Your answer cannot be null.");
				}

				userInputAsByte = Byte.parseByte(userInput);

				if (userInputAsByte < 1 || userInputAsByte > test.getCurrentQuestionNumberOfOptions())
				{
					throw new InvalidStringInputException(String.format(
						"You must input a number between 1 and %d inclusive.",
						test.getCurrentQuestionNumberOfOptions())
					);
				}

				return userInputAsByte;
			}
			catch (NumberFormatException | InvalidStringInputException e)
			{
				showExceptionDialog(e.getMessage());
			}
		}
	}

    private static byte promptForNumberOfQuestionsAndParse()
    {
        String userInput;
        byte userInputAsByte;

        while (true)
        {
            userInput = JOptionPane.showInputDialog(
				null,
				String.format("How many questions would you like on your new test (1 - %d)?",
					TestQuestionBuilder.NUMBER_OF_UNIQUE_QUESTIONS
				)
			);

            try
            {
                if (userInput == null)
                {
                    System.exit(0);
                }

                userInputAsByte = Byte.parseByte(userInput);

                if (userInputAsByte < 1 || userInputAsByte > TestQuestionBuilder.NUMBER_OF_UNIQUE_QUESTIONS)
                {
                    throw new InvalidStringInputException(
						String.format(
							"You must input a number between 1 and %d inclusive.",
							TestQuestionBuilder.NUMBER_OF_UNIQUE_QUESTIONS
						)
					);
                }

				return userInputAsByte;
            }
            catch (NumberFormatException | InvalidStringInputException e)
            {
				showExceptionDialog(e.getMessage());
            }
        }
    }

	private static void showExceptionDialog(String exceptionMessage)
	{
		JOptionPane.showMessageDialog(
			null,
			exceptionMessage,
			"Error!",
			JOptionPane.ERROR_MESSAGE
		);
	}
}