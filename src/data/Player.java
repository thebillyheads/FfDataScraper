package data;

public class Player {

	public int id;
	public String name;
	public int price;
	public String position;
	public int games;
	public String team;
	public double total;
	public int breakEven;
	public int pointsLastRound;

	public Player(int id, String name, String team, String position, int price, int games, int total, int breakEven, int pointsLastRound) {
		this.id = id;
		this.name = name;
		this.team = team;
		this.position = position;
		this.price = price;
		this.games = games;
		this.total = total;
		this.breakEven = breakEven;
		this.pointsLastRound = pointsLastRound;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!Player.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		final Player other = (Player) obj;

		// name
		if ((this.name == null) ? (other.name != null) : !isMatchingName(other.name)) {
			return false;
		}

		// team
		if ((this.team == null) ? (other.team != null) : !this.team.toLowerCase().equals(other.team.toLowerCase())) {
			return false;
		}

		// position
		if ((this.position == null) ? (other.position != null)
				: !this.position.toLowerCase().equals(other.position.toLowerCase())) {
			return false;
		}

		// price
		if (this.price != other.price) {
			return false;
		}

		// games
		if (this.games != other.games) {
			return false;
		}

		// total
		if (this.total != other.total) {
			return false;
		}
		
		// break even
		if (this.breakEven != other.breakEven) {
			return false;
		}
		
		// Points last round
		if (this.pointsLastRound != other.pointsLastRound) {
			return false;
		}
		
		return true;
	}

	private boolean isMatchingName(final String otherName) {
		// Break the names into firstname and surname
		String[] names = this.name.toLowerCase().split(" ");
		String[] otherNames = otherName.toLowerCase().split(" ");
			
		if (names.length != otherNames.length) {
			return false;
		}
		
		if (names.length == 1) {
			return names[0].equals(otherNames[0]);
		}
		
		if (!names[1].equals(otherNames[1])) {
			return false;
		}
		
		if (names[0].length() < otherNames[0].length()) {
			return otherNames[0].substring(0, names[0].length()).equals(names[0]);
		} else {
			return names[0].substring(0, otherNames[0].length()).equals(otherNames[0]);
		}
	}

}
