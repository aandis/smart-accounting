# Testing TODO

Coverage gaps identified in the current test suite, grouped by the tooling
needed to close them.

## Add Robolectric (JVM tests that need Android runtime)

Blocked by lack of an Android runtime shim on the JVM. Adding
`org.robolectric:robolectric` as a `testImplementation` unblocks all of these.

- [ ] `utils/AuthHelper` — `isSignedIn`, `signInUser`, `signOutUser`
      (backed by `SharedPreferences`).
- [ ] `models/*.getInsertUri`, `getDeleteUri`, `getUpdateUri` — all call
      `Uri.parse`, which throws on plain JVM.
- [ ] `utils/NotificationHelper` — channel creation, `simpleNotification`,
      `stickyNotification`, `actionNotification`, `cancelNotification`.
- [ ] `utils/FileUtils.getFullPath` against a real `Context.getFilesDir()`
      (currently only exercised via a mocked Context).

## Add instrumented tests (androidTest) — SQLite / ContentProvider

These exercise the persistence layer against a real `SQLiteDatabase`
provided by the emulator/device. The `androidTest` source set and
`androidx.test` deps are already wired up.

- [ ] `db/AccountingDbHelper` — `onCreate` schema, `onUpgrade`
      migrations under `assets/from_N_to_N+1.sql`, `importDatabase`,
      `exportDatabase`, `dbEmpty`.
- [ ] `db/AccountingProvider` — every URI route (customers, purchases,
      purchase items, credits) for query/insert/update/delete plus
      `applyBatch` used by `Purchase.update`.
- [ ] `models/Customer.insert/update/delete` end-to-end.
- [ ] `models/Purchase.insert/update/delete` including nested
      `PurchaseItem` inserts and the batched update path.
- [ ] `models/PurchaseItem.insert/update` end-to-end.
- [ ] `models/Credit.insert/update/delete` end-to-end.
- [ ] `backup/DbOperation.importDbFromLocal`, `exportDbToLocal` — round
      trip via `FileUtils.copyFile`.
- [ ] Correctness of the SQL views (`calculated_purchases`,
      `total_customer_*`, `customer_dues`) via provider queries.

## Add mocked-network tests (Google Drive)

Requires stubbing the Drive API builder chain. Mockito can do it but the
setup is fragile; consider a wrapper interface if this expands.

- [ ] `utils/DriverServicesHelper.uploadFile` — happy path + IOException.
- [ ] `utils/DriverServicesHelper.searchLatest` — no results, one
      result, multiple results (returns most recent).
- [ ] `utils/DriverServicesHelper.downloadFile` — happy path +
      IOException.

## Add UI tests (Espresso or Maestro)

Best done at the feature level rather than per activity.

- [ ] Sign in → land on Backup screen. Best fit: Maestro (handles the
      Google account picker system dialog).
- [ ] Create purchase → appears in transaction list. Espresso or Maestro.
- [ ] Create credit → adjusts customer due. Espresso or Maestro.
- [ ] Edit purchase / credit — round-trip through
      `PurchaseEditorActivity` / `CreditEditorActivity`.
- [ ] Search flow (`SearchActivity`).
- [ ] Filter flow via `MultiDatePickerFragment` +
      `TransactionListActivity`.
- [ ] Backup export → restore round trip on device
      (`ExportDbService` + `ImportDbService`).

## Custom views

Low priority; these are visual and mostly wrap third-party libraries.

- [ ] `IndianCurrencyEditText`, `MaterialIndianCurrencyEditText`,
      `DecimalFormatterEditText`, `DecimalFormatterTextView`,
      `CurrencyTextView` — formatting behavior with Robolectric.
- [ ] `FloatingActionButtonBehavior` — scroll response with Espresso.
