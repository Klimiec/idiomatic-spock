package idiomaticspock.falsemonicker

import groovy.transform.TupleConstructor
import spock.lang.See
import spock.lang.Specification
import spock.lang.Subject

import java.time.Year

import static idiomaticspock.falsemonicker.Spirit.GIN
import static idiomaticspock.falsemonicker.Spirit.WHISKEY

@See("http://stateyourbizness.blogspot.com/2008/07/good-unit-testing-practice.html")
class FalseMonikerSpec extends Specification {

	@Subject def cocktailFinder = new CocktailFinder()

	def "can find cocktails by base spirit"() {
		given:
		def oldFashioned = new Cocktail("whiskey, bitters and sugar syrup", WHISKEY, Year.of(1860))
		def negroni = new Cocktail("whiskey, vermouth and Campari", GIN, Year.of(1919))
		def whiskeySour = new Cocktail("whiskey, lemon juice and sugar", WHISKEY, Year.of(1870))
		cocktailFinder << oldFashioned << negroni << whiskeySour

		expect:
		def matches = cocktailFinder.findByBaseSpiritNewestFirst()
		matches[0].description == "whiskey, bitters and sugar syrup"
		matches[1].description == "whiskey, lemon juice and sugar"
		matches[2].description == "whiskey, vermouth and Campari"
	}
}

class CocktailFinder {
	@Delegate private final List<Cocktail> cocktails = []

	List<Cocktail> findByBaseSpiritNewestFirst() {
		cocktails.sort { it.invented }
	}
}

@TupleConstructor
class Cocktail {
	final String description
	final Spirit baseSpirit
	final Year invented
}

enum Spirit {
	WHISKEY, GIN, VODKA, RUM
}
