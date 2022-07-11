import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class UserInterface {
	
	
	private DataManager dataManager;
	private Organization org;
	private Scanner in = new Scanner(System.in);
	
	public UserInterface(DataManager dataManager, Organization org) {
		this.dataManager = dataManager;
		this.org = org;
	}
	
	/*
	 * Error handling for incorrect fund name being provided (1.9)
	 * Also displayed proportion of target met (1.3)
	 */
	public void start() {
				
		while (true) {
			System.out.println("\n\n");
			if (org.getFunds().size() > 0) {
				System.out.println("There are " + org.getFunds().size() + " funds in this organization:");
			
				int count = 1;
				for (Fund f : org.getFunds()) {
					
					System.out.println(count + ": " + f.getName());
					
					count++;
				}
				System.out.println("Enter the fund number to see more information.");
			}
			System.out.println("Enter 0 to create a new fund");
			
			String str = in.next();
			
			try {
				if(Integer.parseInt(str) >= 0 && Integer.parseInt(str) <= org.getFunds().size()) {
					int option = Integer.parseInt(str);
					in.nextLine();
					if (option == 0) {
						createFund(); 
					}
					else {
						displayFund(option);
					}
				}
				else {
					System.out.println("Error: Incorrect fund number provided. Please enter a valid number");
				}
			}
			catch(Exception e) {
				System.out.println("Error: Incorrect fund number provided. Please enter a valid number");
			}
		}			
			
	}
	
	/*
	 * Error Handling for creating funds (1.9)
	 */
	public void createFund() {
		
		System.out.print("Enter the fund name: ");
		String name = in.nextLine().trim();
		
		// Check if we have to ensure that the fund name is new 
		List<Fund> funds = org.getFunds();
		List<String> fundNames = new ArrayList<String>();
		for(Fund fund: funds) {
			fundNames.add(fund.getName());
		}
		
		while(name.length() == 0 || fundNames.contains(name)) {
			System.out.println("Error: Incorrect fund name provided! Please provide valid fund name");
			System.out.print("Enter the fund name: ");
			name = in.nextLine().trim(); 
		}
		
		System.out.print("Enter the fund description: ");
		String description = in.nextLine().trim();
		
		while(description.length() == 0) {
			System.out.println("Error: Incorrect fund description provided! Please provide valid description");
			System.out.print("Enter the fund description: ");
			description = in.nextLine().trim(); 
		}
		
		long target = -5;
		do {
			System.out.print("Enter the fund target:");
			while(!in.hasNextInt()) {
				System.out.println("Error: Incorrect target amount provided! Please provide valid target amount");
				System.out.print("Enter the fund target:");
				in.next();
			}
			target = in.nextInt();
		}while(target <= 0 );

	
		Fund fund = dataManager.createFund(org.getId(), name, description, target);
		org.getFunds().add(fund);
		
	}
	
	/*
	 * Changed the date formatting here (1.8)
	 */
	public void displayFund(int fundNumber) {
		
		Fund fund = org.getFunds().get(fundNumber - 1);
		
		System.out.println("\n\n");
		System.out.println("Here is information about this fund:");
		System.out.println("Name: " + fund.getName());
		System.out.println("Description: " + fund.getDescription());
		System.out.println("Target: $" + fund.getTarget());
		
		List<Donation> donations = fund.getDonations();
		System.out.println("Number of donations: " + donations.size());
		long total = 0;
		for (Donation donation : donations) {
			total += donation.getAmount();
			
			String str = donation.getDate().substring(0, 10);
			
			try {
				Date date = new SimpleDateFormat("yyyy-MM-dd").parse(str);
				DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");  
				String out = dateFormat.format(date);
				System.out.println("* " + donation.getContributorName() + ": $" + donation.getAmount() + " on " + out);
			} catch (ParseException e) {

			}
			//System.out.println("* " + donation.getContributorName() + ": $" + donation.getAmount() + " on " + donation.getDate());
		}
	
		double percent = (double)  total/fund.getTarget() * 100;
		
		System.out.println("Total donation amount : $" + total + " (" + percent + "% of target)");
		
		System.out.println("Press the Enter key to go back to the listing of funds");
		in.nextLine();
		
		
		
	}
	
	
	public static void main(String[] args) {
		
		DataManager ds = new DataManager(new WebClient("localhost", 3001));
		
		String login = args[0];
		String password = args[1];
		
		
		Organization org = ds.attemptLogin(login, password);
		
		if (org == null) {
			System.out.println("Login failed.");
		}
		else {

			UserInterface ui = new UserInterface(ds, org);
		
			ui.start();
		
		}
	}

}
