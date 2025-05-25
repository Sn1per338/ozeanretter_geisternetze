Dieses Projekt ist eine Webanwendung zur Erfassung und Verwaltung von Geisternetzen.
Funktionen
	•	Anonyme Meldung von Geisternetzen mit Standort und Größe
	•	Registrierung von Bergungseinsätzen durch berechtigte Nutzende
	•	Statusverfolgung (gemeldet, in Bergung, geborgen, verschollen)
	•	Visualisierung der gemeldeten Netze auf einer Weltkarte
	•	Übersicht aller offenen und abgeschlossenen Bergungsaktivitäten
Technologien
	•	JavaServer Faces (JSF)
	•	Hibernate (ORM)
	•	Maven (Projektmanagement)
	•	PostgreSQL (Datenbank)
	•	Leaflet.js (Kartenvisualisierung)

Die Datei geisternetze_postgresql_dump.backup kann direkt nach Erstellung einer Datenbank ("geisternetze_db")
mit dem PostgreSQL Datenbank-Managementsystem pgAdmin 4 über die Restore-Funktion importiert werden.

Oder alternativ über das Terminal mit folgenden Befehlen:

createdb -U postgres geisternetze_db 		# Erstellung der Datenbank, um Backup wiederherzustellen

pg_restore -U postgres -d geisternetze_db --clean --verbose /mnt/data/geisternetze_postgresql_dump.backup

postgres:password --> bereits in persistence.xml hinterlegt, um einwandfreie Funktionalität zu gewährleiten




