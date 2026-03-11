#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import sys
import time
import urllib.error
import urllib.request
from dataclasses import dataclass
from datetime import datetime
from typing import Any


@dataclass(frozen=True)
class Scenario:
    name: str
    method: str
    path: str
    expected_statuses: tuple[int, ...]
    body: dict[str, Any] | None = None


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Run API integration scenarios and print requests/responses to console."
    )
    parser.add_argument(
        "--base-url",
        default="http://localhost:9080",
        help="API base URL. Default: http://localhost:9080",
    )
    parser.add_argument(
        "--advertisement-id",
        type=int,
        default=1,
        help="Advertisement id for GET/PUT/DELETE scenarios. Default: 1",
    )
    parser.add_argument(
        "--client-id",
        type=int,
        default=None,
        help="Optional client id to attach to advertisement create scenario.",
    )
    parser.add_argument(
        "--timeout",
        type=int,
        default=10,
        help="HTTP timeout in seconds. Default: 10",
    )
    return parser.parse_args()


def make_request(
    method: str,
    url: str,
    timeout: int,
    body: dict[str, Any] | None = None,
) -> tuple[int, str]:
    data = None
    headers = {"Accept": "application/json"}

    if body is not None:
        data = json.dumps(body).encode("utf-8")
        headers["Content-Type"] = "application/json"

    request = urllib.request.Request(url=url, data=data, headers=headers, method=method)

    try:
        with urllib.request.urlopen(request, timeout=timeout) as response:
            return response.status, response.read().decode("utf-8", errors="replace")
    except urllib.error.HTTPError as error:
        return error.code, error.read().decode("utf-8", errors="replace")


def pretty_body(raw_body: str) -> str:
    if not raw_body:
        return "<empty>"

    try:
        parsed = json.loads(raw_body)
    except json.JSONDecodeError:
        return raw_body

    return json.dumps(parsed, ensure_ascii=False, indent=2)


def print_scenario_header(name: str, method: str, url: str) -> None:
    print("=" * 100)
    print(f"Scenario: {name}")
    print(f"Request : {method} {url}")


def build_scenarios(base_url: str, advertisement_id: int, client_id: int | None) -> list[Scenario]:
    suffix = str(int(time.time()))
    now = datetime.now().replace(microsecond=0).isoformat()

    valid_client = {
        "name": f"Client{suffix}",
        "email": f"client{suffix}@example.com",
    }
    invalid_client = {
        "name": f"client{suffix}",
        "email": f"bad{suffix}@example.com",
    }
    notification = {
        "clientName": valid_client["name"],
        "email": valid_client["email"],
        "phoneNumber": "+70000000000",
    }
    advertisement_create = {
        "name": f"Ad{suffix}",
        "category": "OTHER",
        "subcategory": "SMOKE",
        "cost": 100,
        "address": "Test street 1",
        "clientId": client_id,
        "description": "Good item",
        "createDateTime": now,
    }
    advertisement_update = {
        "name": f"UpdatedAd{suffix}",
        "category": "ELECTRONICS",
        "subcategory": "UPDATED",
        "cost": 150,
        "address": "Updated street 2",
        "clientId": client_id,
        "description": "Updated description",
        "createDateTime": now,
    }

    return [
        Scenario(
            name="Create client (valid)",
            method="POST",
            path="/v1/clients",
            body=valid_client,
            expected_statuses=(201,),
        ),
        Scenario(
            name="Create client (invalid lowercase name)",
            method="POST",
            path="/v1/clients",
            body=invalid_client,
            expected_statuses=(400,),
        ),
        Scenario(
            name="Send notification",
            method="POST",
            path="/v1/notification",
            body=notification,
            expected_statuses=(200,),
        ),
        Scenario(
            name="Create advertisement",
            method="POST",
            path="/v1/advertisements",
            body=advertisement_create,
            expected_statuses=(201,),
        ),
        Scenario(
            name="Get advertisement by configured id",
            method="GET",
            path=f"/v1/advertisements/{advertisement_id}",
            expected_statuses=(200,),
        ),
        Scenario(
            name="Get advertisement with invalid id",
            method="GET",
            path="/v1/advertisements/0",
            expected_statuses=(400,),
        ),
        Scenario(
            name="Update advertisement",
            method="PUT",
            path=f"/v1/advertisements/{advertisement_id}",
            body=advertisement_update,
            expected_statuses=(200,),
        ),
        Scenario(
            name="Delete advertisement",
            method="DELETE",
            path=f"/v1/advertisements/{advertisement_id}",
            expected_statuses=(200,),
        ),
    ]


def run() -> int:
    args = parse_args()
    base_url = args.base_url.rstrip("/")
    scenarios = build_scenarios(base_url, args.advertisement_id, args.client_id)

    unexpected = 0

    for scenario in scenarios:
        url = f"{base_url}{scenario.path}"
        print_scenario_header(scenario.name, scenario.method, url)

        if scenario.body is not None:
            print("Payload :")
            print(json.dumps(scenario.body, ensure_ascii=False, indent=2))

        try:
            status, raw_body = make_request(
                method=scenario.method,
                url=url,
                timeout=args.timeout,
                body=scenario.body,
            )
        except urllib.error.URLError as error:
            unexpected += 1
            print(f"Transport error: {error}")
            continue
        except Exception as error:  # pragma: no cover
            unexpected += 1
            print(f"Unexpected exception: {error}")
            continue

        print(f"Status  : {status}")
        print("Response:")
        print(pretty_body(raw_body))

        if status not in scenario.expected_statuses:
            unexpected += 1
            expected = ", ".join(str(code) for code in scenario.expected_statuses)
            print(f"Result  : FAILED (expected status: {expected})")
        else:
            print("Result  : OK")

    print("=" * 100)
    if unexpected:
        print(f"Summary : FAILED, unexpected scenarios = {unexpected}")
        return 1

    print("Summary : OK, all scenarios returned expected statuses")
    return 0


if __name__ == "__main__":
    sys.exit(run())
