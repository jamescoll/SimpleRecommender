import java.util.*;
import java.io.*;
import java.math.BigDecimal;

class User {
	// a list of the Ratings the user has given
	ArrayList<Rating> ratings;
	int userNumber;
	String userNameKey;

	User() {

	}

	User(int userNumber, String userNameKey) {
		this.userNumber = userNumber;
		this.userNameKey = userNameKey;
		this.ratings = new ArrayList<Rating>();
	}

	public ArrayList<Rating> getRatings() {
		return this.ratings;
	}

	public void addRating(Rating r) {
		ratings.add(r);
	}

	public int getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(int userNumber) {
		this.userNumber = userNumber;
	}

	public String getUserNameKey() {
		return userNameKey;
	}

	public void setUserNameKey(String userNameKey) {
		this.userNameKey = userNameKey;
	}

}

class Rating {

	User user;
	Movie movie;
	double value;

	Rating() {

		this.value = 0.0;
	}

	Rating(double value) {
		this.value = value;
	}

	Rating(User user, Movie movie, double value) {

		this.user = user;
		this.movie = movie;
		this.value = value;
		user.addRating(this);
		movie.addRating(this);

	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Movie getMovie() {
		return movie;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

}

class Movie {
	// a list of the ratings given to the movie
	ArrayList<Rating> ratings;

	int movieId;

	Movie() {

	}

	Movie(int movieId) {
		this.movieId = movieId;
		this.ratings = new ArrayList<Rating>();

	}

	public ArrayList<Rating> getRatings() {
		return ratings;
	}

	public void addRating(Rating r) {
		ratings.add(r);
	}

	public int getMovieId() {
		return movieId;
	}

	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}

}

class NonPersonalisedRecommender {

	public static void main(String[] args) {

		int movieA = 11;
		int movieB = 121;
		int movieC = 8587;

		// Movies array contains the movie IDs of the top 5 movies.
		ArrayList<Movie> topFiveA = new ArrayList<Movie>();
		ArrayList<Movie> topFiveB = new ArrayList<Movie>();
		ArrayList<Movie> topFiveC = new ArrayList<Movie>();

		// Buffered reader for input file reading
		BufferedReader br = null;

		// String constants for csv file reading
		String line = "";
		String csvSplitBy = ",";

		// HashMaps of Users and Ratings - id is the uId and mId
		HashMap<Integer, User> userMap = new HashMap<Integer, User>();
		HashMap<Integer, Movie> movieMap = new HashMap<Integer, Movie>();

		try {

			br = new BufferedReader(new FileReader("recsys-data-ratings.csv"));
			while ((line = br.readLine()) != null) {
				String[] item = line.split(csvSplitBy);

				User u;
				Movie m;

				int uId = Integer.parseInt(item[0]);
				// this is a hack to deal with the different file formats
				String sId = item[0];
				int mId = Integer.parseInt(item[1]);
				double rating = Double.parseDouble(item[2]);

				if (userMap.containsKey(uId)) {
					u = userMap.get(uId);
				} else {

					u = new User(uId, sId);
					userMap.put(uId, u);
				}

				if (movieMap.containsKey(mId)) {
					m = movieMap.get(mId);
				} else {
					m = new Movie(mId);
					movieMap.put(mId, m);
				}

				// instantiate the rating object
				Rating r = new Rating(u, m, rating);

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// hashmaps containing all the coRatings for movie A, movie B and movie
		// C
		HashMap<Movie, Double> coRatingsA = new HashMap<Movie, Double>();
		HashMap<Movie, Double> coRatingsB = new HashMap<Movie, Double>();
		HashMap<Movie, Double> coRatingsC = new HashMap<Movie, Double>();

		// hashmaps containing all the coRatings for movie A, movie B and movie
		// C
		HashMap<Movie, Double> coRatingsAdvancedA = new HashMap<Movie, Double>();
		HashMap<Movie, Double> coRatingsAdvancedB = new HashMap<Movie, Double>();
		HashMap<Movie, Double> coRatingsAdvancedC = new HashMap<Movie, Double>();

		// treemaps which impose ordering on those HashMaps this WILL FAIL if
		// there are duplicate values
		TreeMap<Double, Movie> sortedA = new TreeMap<Double, Movie>();
		TreeMap<Double, Movie> sortedB = new TreeMap<Double, Movie>();
		TreeMap<Double, Movie> sortedC = new TreeMap<Double, Movie>();
		
		//treemaps to contain advanced co-Ratings - same thing as above applies
		TreeMap<Double, Movie> sortedAdA = new TreeMap<Double, Movie>();
		TreeMap<Double, Movie> sortedAdB = new TreeMap<Double, Movie>();
		TreeMap<Double, Movie> sortedAdC = new TreeMap<Double, Movie>();

		// we need the number of users for the second formula
		int numUsers = userMap.size();

		// getting the coRatings
		populateCoRatings(movieA, movieMap, coRatingsA, coRatingsAdvancedA,
				numUsers);
		populateCoRatings(movieB, movieMap, coRatingsB, coRatingsAdvancedB,
				numUsers);
		populateCoRatings(movieC, movieMap, coRatingsC, coRatingsAdvancedC,
				numUsers);

		// sorting them by copying them into TreeMaps
		sortedA = sortCoRatings(coRatingsA);
		sortedB = sortCoRatings(coRatingsB);
		sortedC = sortCoRatings(coRatingsC);
		
		sortedAdA = sortCoRatings(coRatingsAdvancedA);
		sortedAdB = sortCoRatings(coRatingsAdvancedB);
		sortedAdC = sortCoRatings(coRatingsAdvancedC);
		

		// Write the top 5 movies, one per line, to a text file.
		try {
			PrintWriter writer = new PrintWriter("pa1-result.txt", "UTF-8");

			int counter = 0;

			Iterator<Double> movieASetIterator = sortedA.keySet().iterator();
			writer.print(movieA);
			while (movieASetIterator.hasNext() && counter != 5) {

				Double key = movieASetIterator.next();
				writer.print("," + sortedA.get(key).getMovieId());
				double a = round(key, 2);
				writer.print("," + a);
				counter++;

			}
			writer.print("\n");

			counter = 0;
			Iterator<Double> movieBSetIterator = sortedB.keySet().iterator();
			writer.print(movieB);
			while (movieBSetIterator.hasNext() && counter != 5) {

				Double key = movieBSetIterator.next();
				writer.print("," + sortedB.get(key).getMovieId());
				double a = round(key, 2);
				writer.print("," + a);
				counter++;

			}
			writer.print("\n");

			counter = 0;
			Iterator<Double> movieCSetIterator = sortedC.keySet().iterator();
			writer.print(movieC);
			while (movieCSetIterator.hasNext() && counter != 5) {

				Double key = movieCSetIterator.next();
				writer.print("," + sortedC.get(key).getMovieId());
				double a = round(key, 2);
				writer.print("," + a);
				counter++;

			}
			writer.print("\n");
			
			counter = 0;

			Iterator<Double> movieA1SetIterator = sortedAdA.keySet().iterator();
			writer.print(movieA);
			while (movieA1SetIterator.hasNext() && counter != 5) {

				Double key = movieA1SetIterator.next();
				writer.print("," + sortedAdA.get(key).getMovieId());
				double a = round(key, 2);
				writer.print("," + a);
				counter++;

			}
			writer.print("\n");

			counter = 0;
			Iterator<Double> movieB1SetIterator = sortedAdB.keySet().iterator();
			writer.print(movieB);
			while (movieB1SetIterator.hasNext() && counter != 5) {

				Double key = movieB1SetIterator.next();
				writer.print("," + sortedAdB.get(key).getMovieId());
				double a = round(key, 2);
				writer.print("," + a);
				counter++;

			}
			writer.print("\n");

			counter = 0;
			Iterator<Double> movieC1SetIterator = sortedAdC.keySet().iterator();
			writer.print(movieC);
			while (movieC1SetIterator.hasNext() && counter != 5) {

				Double key = movieC1SetIterator.next();
				writer.print("," + sortedAdC.get(key).getMovieId());
				double a = round(key, 2);
				writer.print("," + a);
				counter++;

			}
			writer.print("\n");
			
			

			writer.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// THIS IS NOT THE OPTIMAL SOLUTION.. IN THE UNLIKELY EVENT (FOR LARGE DATA)
	// THAT THERE ARE DUPLICATE RATINGS VALUES - THIS WILL BREAK
	// DUPLICATES ON ZERO ARE ALSO POSSIBLE AND MORE LIKELY
	public static TreeMap<Double, Movie> sortCoRatings(
			HashMap<Movie, Double> coRatings) {
		// this function takes a list of CoRatings and sorts it according to the
		// double value
		TreeMap<Double, Movie> sortedMap = new TreeMap<Double, Movie>(
				Collections.reverseOrder());
		// to do this we will effectively flip the HashMap into a TreeMap on
		// which we can compare values
		for (Map.Entry<Movie, Double> entry : coRatings.entrySet()) {
			Movie m = entry.getKey();
			Double d = entry.getValue();
			sortedMap.put(d, m);
		}
		return sortedMap;

	}

	public static void populateCoRatings(int inputMovie,
			HashMap<Integer, Movie> movieMap, HashMap<Movie, Double> coRatings,
			HashMap<Movie, Double> coRatingsAdvanced, int numUsers) {
		// for each rating belonging to the specific film
		for (Rating r : movieMap.get(inputMovie).getRatings()) {

			// for each user belonging to that rating
			User u = r.getUser();

			// get the set of ratings given by that user
			for (Rating sr : u.getRatings()) {
				// if the movie is not equal to the input movie
				if (sr.getMovie().getMovieId() != inputMovie) {
					// check if the movie is already in the CoRatings list
					if (coRatings.containsKey(sr.getMovie())) {
						// if it is add one to the count
						double d = coRatings.get(sr.getMovie());
						coRatings.put(sr.getMovie(), d + 1.0);
					} else {
						// if it ain't then put it there with a one value
						coRatings.put(sr.getMovie(), 1.0);
					}
				}

			}

		}

		// remove the input movie from the coRatings map
		// Movie m = movieMap.get(inputMovie);
		// coRatings.remove(m);

		double div = numUsers - movieMap.get(inputMovie).getRatings().size();

		// the denominator of the function which gives us x+y/x//!x+y/!x
		// we can get the numerator after calculating the final coRating value
		for (Map.Entry<Movie, Double> entry : coRatings.entrySet()) {
			Movie m1 = entry.getKey();
			// this is the number of people who have rated given movie but not
			// input movie
			double a = m1.getRatings().size() - coRatings.get(m1);
			double b = a / div;
			coRatingsAdvanced.put(m1, b);

		}

		// this will calculate the final co-rating value and save some work in
		// main
		Iterator<Integer> movieSetIterator = movieMap.keySet().iterator();
		while (movieSetIterator.hasNext()) {
			Integer key = movieSetIterator.next();
			if (movieMap.get(key).getMovieId() != inputMovie) {
				double a = coRatings.get(movieMap.get(key));
				double b = movieMap.get(inputMovie).getRatings().size() * 1.0;
				double c = a / b;
				// double d = round(c, 2);
				coRatings.put(movieMap.get(key), c);
			} else {
				coRatings.put(movieMap.get(key), 0.0);
			}

		}

		// now we can work out the advanced coRatings value
		for (Map.Entry<Movie, Double> entry : coRatingsAdvanced.entrySet()) {
			Movie m1 = entry.getKey();
			// this is the number of people who have rated given movie but not
			// input movie
			double a = coRatings.get(m1);
			double b = coRatingsAdvanced.get(m1).doubleValue();
			double c = a / b;
			coRatingsAdvanced.put(m1, c);

		}

	}

	// this will allow us to round the double values
	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

}
