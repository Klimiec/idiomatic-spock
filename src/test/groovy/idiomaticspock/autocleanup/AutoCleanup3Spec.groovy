package idiomaticspock.autocleanup

import org.skife.jdbi.v2.DBI
import org.skife.jdbi.v2.Handle
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

class AutoCleanup3Spec extends Specification {

	@Shared dbi = new DBI("jdbc:h2:mem:test")
	@Shared @AutoCleanup Handle handle
	@Shared @Subject @AutoCleanup("dropTable") CocktailStore cocktailStore

	def setupSpec() {
		handle = dbi.open()
		cocktailStore = handle.attach(CocktailStore)
		cocktailStore.createTable()
	}

	def "can retrieve a list of cocktails"() {
		given:
		def statement = handle.createStatement("insert into cocktail (name, base_spirit) values (?, ?)")
		[Gin: ["Negroni", "Aviation"], Whiskey: ["Old Fashioned"]].each { baseSpirit, names ->
			names.each { name ->
				statement.bind(0, name).bind(1, baseSpirit).execute()
			}
		}

		when:
		def cocktails = cocktailStore.listCocktails("Gin")

		then:
		with(cocktails.toList()) {
			size() == 2
			baseSpirit.every { it == "Gin" }
			name == ["Negroni", "Aviation"]
		}
	}
}



