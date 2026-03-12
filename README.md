# -SolarLab.Academy.Java-2026.02
Академия Solarlab "Java" 2026 - Учебный пример 

## Run Gradle Commands

Use the root script `service.bat`:

```bat
service.bat compose-up
service.bat compose-down
service.bat assemble
service.bat test
service.bat build
service.bat run
```

Requirements:

- JDK 21+
- Gradle in `PATH` if `gradlew.bat` is absent
- PostgreSQL on `localhost:5432` for `service.bat run`

## API Integration Script

With the service running, execute:

```bat
python scripts\api_integration_test.py --base-url http://localhost:9080
```

Optional arguments:

- `--advertisement-id 1`
- `--client-id 1`
- `--timeout 10`
