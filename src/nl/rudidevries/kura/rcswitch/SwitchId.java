package nl.rudidevries.kura.rcswitch;

class SwitchId {

	private String param1;
	private String param2;
	
	SwitchId(String param1, String param2) {
		this.param1 = param1;
		this.param2 = param2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((param1 == null) ? 0 : param1.hashCode());
		result = prime * result + ((param2 == null) ? 0 : param2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SwitchId other = (SwitchId) obj;
		if (param1 == null) {
			if (other.param1 != null)
				return false;
		} else if (!param1.equals(other.param1))
			return false;
		if (param2 == null) {
			if (other.param2 != null)
				return false;
		} else if (!param2.equals(other.param2))
			return false;
		return true;
	}



	public String toString() {
		return String.format("%s %s", param1, param2);
	}
}
