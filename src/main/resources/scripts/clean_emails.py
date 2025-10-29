import json, re, sys

def clean_email_data(input_path, output_path):
    email_regex = re.compile(r"([a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+)")
    with open(input_path, "r", encoding="utf-8") as f:
        data = json.load(f)

    cleaned = []
    for rec in data:
        email = rec.get("email", "")
        if email_regex.match(email):
            cleaned.append(rec)

    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(cleaned, f, indent=2)

if __name__ == "__main__":
    clean_email_data(sys.argv[1], sys.argv[2])
