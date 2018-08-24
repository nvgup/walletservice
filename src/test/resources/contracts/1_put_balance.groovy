package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'PUT'
        url '/player/1/balance'
        body([
                balance: "-1"
        ])
        headers {
            contentType('application/json')
        }
    }
    response {
        status 200
        body([
                balance: "9"
        ])
        headers {
            contentType('application/json')
        }
    }
}