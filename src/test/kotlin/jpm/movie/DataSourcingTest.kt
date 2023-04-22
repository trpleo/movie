package jpm.movie

import org.junit.jupiter.api.Test

class DataSourcingTest {
    @Test
    fun `should pick up valid data from the bucket`() {
        // given
        // application spin up and
        // connected to the bucket

        // when
        // data valid pushed into the bucket

        // then
        // data will be read and
        // data is serialized and
        // data is stored in the journal
    }

    @Test
    fun `should pick up data from the bucket and log it if mailformed`() {

    }

    @Test
    fun `should pick up data from bucket and continue processing even if data was mailformed`() {
        // given
        // application spin up and
        // connected to the bucket
        // valid data pushed to bucket (and processed)

        // when
        // invalid data pushed into the bucket

        // then
        // data will be read and
        // data is serialized with error and
        // data is not stored to journal
        // error is printed out.
    }
}